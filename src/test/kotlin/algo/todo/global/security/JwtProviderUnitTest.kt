package algo.todo.global.security

import algo.todo.domain.user.entity.Users
import algo.todo.domain.user.repository.UserRepository
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletResponse
import org.springframework.test.util.ReflectionTestUtils

class JwtProviderUnitTest : DescribeSpec({

    val userRepository = mockk<UserRepository>()

    // 테스트용 secret / 만료시간
    val secretKey = "testSecretKeytestSecretKeytestSecretKeytestSecretKeytestSecretKey"
    val accessExpMillis = 60_000L // 1분
    val refreshExpMillis = 120_000L // 2분

    val jwtProvider =
        JwtProvider(
            secret = secretKey,
            accessTokenExpirationInMillis = accessExpMillis,
            refreshTokenExpirationInMillis = refreshExpMillis,
            userRepository = userRepository,
        )

    fun createTestUser(): Users {
        val user =
            Users(
                email = "test@example.com",
                providerType = ProviderType.GOOGLE,
                providerId = "testProviderId",
            )

        ReflectionTestUtils.setField(user, "id", 1L)

        return user
    }

    describe("generateAccessToken 메서드") {
        context("유효한 사용자 정보가 주어지면") {
            it("유효한 JWT Access 토큰을 생성해야 한다") {
                // given
                val user = createTestUser()

                // when
                val token = jwtProvider.generateAccessToken(user)

                // then
                token shouldNotBe null
                token.isNotBlank() shouldBe true
            }
        }
    }

    describe("generateRefreshToken 메서드") {
        context("Users 객체가 주어졌을 때") {
            it("유효한 Refresh Token 문자열을 생성한다") {
                // given
                val user = createTestUser()

                // when
                val token = jwtProvider.generateRefreshToken(user)

                // then
                token shouldNotBe null
                token.isNotBlank() shouldBe true
            }
        }
    }

    describe("ensureValidToken 메서드") {
        context("유효한 토큰이 주어지면") {
            it("예외가 발생하지 않는다") {
                // given
                val user = createTestUser()
                val accessToken = jwtProvider.generateAccessToken(user)

                // when & then
                jwtProvider.ensureValidToken(accessToken)
            }
        }

        context("잘못된 토큰이 주어지면") {
            it("CustomException[INVALID_TOKEN] 이 발생한다") {
                // given
                val invalidToken = "xx.yy.zz"

                // when & then
                val ex =
                    shouldThrow<CustomException> {
                        jwtProvider.ensureValidToken(invalidToken)
                    }

                ex.errorType shouldBe ErrorType.INVALID_TOKEN
                ex.domainCode shouldBe DomainCode.COMMON
            }
        }

        describe("getAuthentication 메서드") {
            val response = mockk<HttpServletResponse>(relaxed = true)

            context("유효한 Access Token 이면서, DB 에서 사용자 조회에 성공한 경우") {
                it("Authentication 객체를 반환한다") {
                    // given
                    val user = createTestUser()
                    val token = jwtProvider.generateAccessToken(user)

                    every { userRepository.findByIdAndEmail(1L, "test@example.com") } returns user

                    // when
                    val result = jwtProvider.getAuthentication(token, response)

                    // then
                    result.isSuccess shouldBe true
                    val auth = result.getOrNull()
                    auth?.name shouldBe "test@example.com"
                }
            }

            context("유효하지 않은 Access Token 일 때") {
                it("CustomException[INVALID_TOKEN] 이 발생한다") {
                    // given
                    val invalidToken = "xx.yy.zz"

                    // when
                    val ex =
                        shouldThrow<CustomException> {
                            jwtProvider.getAuthentication(invalidToken, response)
                        }

                    // then
                    ex.errorType shouldBe ErrorType.INVALID_TOKEN
                    ex.domainCode shouldBe DomainCode.COMMON
                }
            }

            context("유효한 토큰이지만 사용자 조회에 실패한 경우") {
                it("CustomException[INVALID_TOKEN] 이 발생한다") {
                    // given
                    val user = createTestUser()
                    val token = jwtProvider.generateAccessToken(user)

                    every { userRepository.findByIdAndEmail(any(), any()) } returns null

                    // when
                    val ex =
                        shouldThrow<CustomException> {
                            jwtProvider.getAuthentication(token, response)
                        }

                    // then
                    ex.errorType shouldBe ErrorType.INVALID_TOKEN
                    ex.domainCode shouldBe DomainCode.COMMON
                }
            }

            context("Refresh Token 을 전달한 경우") {
                it("ensureIsAccessToken 에서 실패하여 CustomException[INVALID_TOKEN] 이 발생한다") {
                    // given
                    val user = createTestUser()
                    val refreshToken = jwtProvider.generateRefreshToken(user)

                    // when
                    val ex =
                        shouldThrow<CustomException> {
                            jwtProvider.getAuthentication(refreshToken, response)
                        }

                    // then
                    ex.errorType shouldBe ErrorType.INVALID_TOKEN
                    ex.domainCode shouldBe DomainCode.COMMON
                }
            }

            context("필수 claims 가 존재하지 않을 경우") {
                it("CustomException[INVALID_TOKEN] 이 발생한다") {
                    // given
                    val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

                    val invalidToken =
                        Jwts.builder()
//                        .claim("id", 1L) // id 는 필수 claim
                            .claim("token_type", TokenType.ACCESS)
                            .subject("testUser@gmail.com")
                            .signWith(key)
                            .compact()

                    // when & then
                    val ex =
                        shouldThrow<CustomException> {
                            jwtProvider.getAuthentication(invalidToken, response)
                        }

                    ex.errorType shouldBe ErrorType.INVALID_TOKEN
                    ex.domainCode shouldBe DomainCode.COMMON
                }
            }
        }
    }
})
