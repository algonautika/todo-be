package algo.todo.domain.user.service

import algo.todo.domain.user.entity.Users
import algo.todo.domain.user.repository.UserRepository
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode
import algo.todo.global.security.ProviderType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.test.util.ReflectionTestUtils

class UserServiceUnitTest : DescribeSpec({

    val userRepository = mockk<UserRepository>()

    val userService = UserService(userRepository)

    fun makeOAuth2AuthToken(
        registrationId: String,
        attributes: Map<String, Any>,
    ): OAuth2AuthenticationToken {
        val authorities = emptyList<org.springframework.security.core.GrantedAuthority>()
        val oAuth2User: OAuth2User = DefaultOAuth2User(authorities, attributes, "sub")
        return OAuth2AuthenticationToken(oAuth2User, authorities, registrationId)
    }

    fun createTestUser(
        id: Long,
        email: String,
        providerType: ProviderType,
        providerId: String,
    ): Users {
        val user =
            Users(
                email = email,
                providerType = providerType,
                providerId = providerId,
            )

        ReflectionTestUtils.setField(user, "id", id)

        return user
    }

    describe("loadOrCreateUser 메서드") {
        context("사용자가 존재하지 않을 경우") {
            it("이메일이 이미 존재한다면 CANNOT_CHANGE_EMAIL 에러를 던진다") {
                // given
                val authToken =
                    makeOAuth2AuthToken(
                        registrationId = "google",
                        attributes =
                            mapOf(
                                "sub" to "testProviderId",
                                "email" to "duplicate@example.com",
                            ),
                    )

                every {
                    userRepository.findByProviderTypeAndProviderId(ProviderType.GOOGLE, "testProviderId")
                } returns null

                val existingUser =
                    createTestUser(
                        id = 1,
                        email = "duplicate@example.com",
                        providerType = ProviderType.GOOGLE,
                        providerId = "someOther",
                    )
                every { userRepository.findByEmail("duplicate@example.com") } returns existingUser

                // when & then
                val ex =
                    shouldThrow<CustomException> {
                        userService.loadOrCreateUser(authToken)
                    }

                ex.errorType shouldBe ErrorType.CANNOT_CHANGE_EMAIL
                ex.domainCode shouldBe DomainCode.COMMON
            }

            it("이메일이 존재하지 않는다면 새로 저장하고 반환한다") {
                // given
                val authToken =
                    makeOAuth2AuthToken(
                        registrationId = "google",
                        attributes =
                            mapOf(
                                "sub" to "testProviderId",
                                "email" to "newuser@example.com",
                            ),
                    )

                every {
                    userRepository.findByProviderTypeAndProviderId(ProviderType.GOOGLE, "testProviderId")
                } returns null

                every { userRepository.findByEmail("newuser@example.com") } returns null

                val savedUser =
                    createTestUser(
                        id = 1,
                        email = "newuser@example.com",
                        providerType = ProviderType.GOOGLE,
                        providerId = "testProviderId2",
                    )
                every { userRepository.save(any<Users>()) } returns savedUser

                // when
                val result = userService.loadOrCreateUser(authToken)

                // then
                result shouldNotBe null
                result.email shouldBe "newuser@example.com"
                result.id shouldBe 1
            }
        }

        context("사용자가 이미 존재할 경우") {
            it("새로운 이메일과 기존 이메일이 다르고, 새 이메일이 이미 존재하면 ALREADY_EXIST_EMAIL 던진다") {
                // given
                val existingUser =
                    createTestUser(
                        id = 99,
                        email = "old@example.com",
                        providerType = ProviderType.GOOGLE,
                        providerId = "existingProviderId",
                    )

                val authToken =
                    makeOAuth2AuthToken(
                        registrationId = "google",
                        attributes =
                            mapOf(
                                "sub" to "existingProviderId",
                                "email" to "somebody@example.com",
                            ),
                    )

                every {
                    userRepository.findByProviderTypeAndProviderId(ProviderType.GOOGLE, "existingProviderId")
                } returns existingUser

                every { userRepository.findByEmail("somebody@example.com") } returns
                    createTestUser(
                        id = 55,
                        email = "somebody@example.com",
                        providerType = ProviderType.GOOGLE,
                        providerId = "someOther",
                    )

                // when & then
                val ex =
                    shouldThrow<CustomException> {
                        userService.loadOrCreateUser(authToken)
                    }
                ex.errorType shouldBe ErrorType.ALREADY_EXIST_EMAIL
                ex.domainCode shouldBe DomainCode.COMMON
            }

            it("새로운 이메일과 기존 이메일이 다르고, 새 이메일이 존재하지 않으면 사용자 이메일을 변경하고 저장한다") {
                // given
                val existingUser =
                    createTestUser(
                        id = 99,
                        email = "old@example.com",
                        providerType = ProviderType.GOOGLE,
                        providerId = "existingProviderId",
                    )

                val authToken =
                    makeOAuth2AuthToken(
                        registrationId = "google",
                        attributes =
                            mapOf(
                                "sub" to "existingProviderId",
                                "email" to "changed@example.com",
                            ),
                    )
                every {
                    userRepository.findByProviderTypeAndProviderId(ProviderType.GOOGLE, "existingProviderId")
                } returns existingUser

                every { userRepository.findByEmail("changed@example.com") } returns null

                val updatedUser =
                    createTestUser(
                        id = 99,
                        email = "changed@example.com",
                        providerType = ProviderType.GOOGLE,
                        providerId = "existingProviderId",
                    )

                every { userRepository.save(existingUser) } returns updatedUser

                // when
                val result = userService.loadOrCreateUser(authToken)

                // then
                result.email shouldBe "changed@example.com"
                result.id shouldBe 99
            }

            it("새로운 이메일과 기존 이메일이 같다면(변경 없음), 그냥 기존 사용자를 반환한다") {
                // given
                val existingUser =
                    createTestUser(
                        id = 99,
                        email = "old@example.com",
                        providerType = ProviderType.GOOGLE,
                        providerId = "existingProviderId",
                    )

                val authToken =
                    makeOAuth2AuthToken(
                        registrationId = "google",
                        attributes =
                            mapOf(
                                "sub" to "existingProviderId",
                                "email" to "old@example.com",
                            ),
                    )
                every {
                    userRepository.findByProviderTypeAndProviderId(ProviderType.GOOGLE, "existingProviderId")
                } returns existingUser

                every { userRepository.findByEmail("old@example.com") } returns null

                // when
                val result = userService.loadOrCreateUser(authToken)

                // then
                result shouldBe existingUser
            }
        }

        context("token 의 ")
    }
})
