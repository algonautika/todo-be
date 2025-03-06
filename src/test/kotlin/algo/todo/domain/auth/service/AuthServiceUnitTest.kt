package algo.todo.domain.auth.service

import algo.todo.domain.auth.controller.dto.request.ReIssueRequestDto
import algo.todo.domain.auth.entity.RefreshToken
import algo.todo.domain.auth.repository.AuthRepository
import algo.todo.domain.user.entity.Users
import algo.todo.global.dto.DomainCode
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.security.JwtProvider
import algo.todo.global.security.ProviderType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AuthServiceUnitTest : DescribeSpec({

    val authRepository = mockk<AuthRepository>()
    val jwtProvider = mockk<JwtProvider>()

    val authService = AuthService(authRepository, jwtProvider)

    describe("reIssueToken 메서드") {
        context("유효한 refreshToken") {
            it("새로운 accessToken, refreshToken 을 반환하고 DB를 업데이트한다") {
                // given
                val refreshTokenValue = "validRefreshToken"
                val user = Users(
                    email = "test@example.com",
                    providerType = ProviderType.GOOGLE,
                    providerId = "providerId"
                )
                val savedRefreshToken = RefreshToken(
                    users = user,
                    refreshToken = refreshTokenValue
                )

                val requestDto = ReIssueRequestDto(refreshTokenValue)

                every { authRepository.findByRefreshToken(refreshTokenValue) } returns savedRefreshToken
                every { jwtProvider.ensureValidToken(refreshTokenValue) } returns Unit

                val newAccessToken = "newAccessToken"
                val newRefreshToken = "newRefreshToken"
                every { jwtProvider.generateAccessToken(user) } returns newAccessToken
                every { jwtProvider.generateRefreshToken(user) } returns newRefreshToken

                // when
                val result = authService.reIssueToken(requestDto)

                // then
                result.accessToken shouldBe newAccessToken
                result.refreshToken shouldBe newRefreshToken
                savedRefreshToken.refreshToken shouldBe newRefreshToken

                verify(exactly = 1) {
                    authRepository.findByRefreshToken(refreshTokenValue)
                    jwtProvider.ensureValidToken(refreshTokenValue)
                    jwtProvider.generateAccessToken(user)
                    jwtProvider.generateRefreshToken(user)
                }
            }
        }

        context("존재하지 않는 refreshToken") {
            it("CustomException(INVALID_TOKEN)을 던진다") {
                // given
                val requestDto = ReIssueRequestDto("nonexistentToken")
                every { authRepository.findByRefreshToken("nonexistentToken") } returns null

                // when & then
                val ex = shouldThrow<CustomException> {
                    authService.reIssueToken(requestDto)
                }

                ex.errorType shouldBe ErrorType.INVALID_TOKEN
                ex.domainCode shouldBe DomainCode.COMMON
            }
        }

        context("refreshToken 이 유효하지 않은 경우") {
            it("CustomException(INVALID_TOKEN)을 던진다") {
                // given
                val invalidTokenValue = "invalidToken"
                val user = Users(
                    email = "user@example.com",
                    providerType = ProviderType.GOOGLE,
                    providerId = "someProvider"
                )
                val refreshTokenEntity = RefreshToken(
                    users = user,
                    refreshToken = invalidTokenValue
                )
                val requestDto = ReIssueRequestDto(invalidTokenValue)

                every { authRepository.findByRefreshToken(invalidTokenValue) } returns refreshTokenEntity
                // ensureValidToken이 예외 발생
                every { jwtProvider.ensureValidToken(invalidTokenValue) } throws CustomException(
                    ErrorType.INVALID_TOKEN,
                    DomainCode.COMMON
                )

                // when & then
                val ex = shouldThrow<CustomException> {
                    authService.reIssueToken(requestDto)
                }
                ex.errorType shouldBe ErrorType.INVALID_TOKEN
                ex.domainCode shouldBe DomainCode.COMMON
            }
        }
    }

    describe("upsertRefreshToken 메서드") {
        context("이미 해당 Users 의 RefreshToken 이 있으면") {
            it("기존 RefreshToken 을 업데이트한다") {
                // given
                val user = Users(
                    email = "existingUser@example.com",
                    providerType = ProviderType.GOOGLE,
                    providerId = "someProviderId"
                )
                val oldToken = RefreshToken(
                    users = user,
                    refreshToken = "oldRefresh"
                )
                val newTokenValue = "newRefresh"

                every { authRepository.findByUsers(user) } returns oldToken

                // when
                authService.upsertRefreshToken(user, newTokenValue)

                // then
                oldToken.refreshToken shouldBe newTokenValue

                verify(exactly = 1) {
                    authRepository.findByUsers(user)
                }
            }
        }

        context("해당 Users의 RefreshToken이 없다면") {
            it("새로운 RefreshToken 엔티티를 저장한다") {
                // given
                val user = Users(
                    email = "noRefreshUser@example.com",
                    providerType = ProviderType.GOOGLE,
                    providerId = "none"
                )
                val newTokenValue = "someRefreshValue"

                every { authRepository.findByUsers(user) } returns null
                every { authRepository.save(any<RefreshToken>()) } answers { firstArg() }

                // when
                authService.upsertRefreshToken(user, newTokenValue)

                // then
                verify {
                    authRepository.findByUsers(user)
                    authRepository.save(withArg<RefreshToken> {
                        it.users shouldBe user
                        it.refreshToken shouldBe newTokenValue
                    })
                }
            }
        }
    }

})
