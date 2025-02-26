package algo.todo.domain.user.service

import algo.todo.domain.user.entity.Users
import algo.todo.domain.user.repository.UserRepository
import algo.todo.global.dto.DomainCode
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.security.ProviderType
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    /**
     * 분기 처리
     * 1. 조회된 사용자가 없을 경우
     * - 1.1 email 이 이미 존재할 경우 -> 이미 사용중인 이메일로 회원가입 불가
     * - 1.2 email 이 존재하지 않을 경우 -> 사용자 정보 생성 후 사용자 정보 반환
     *
     * 2. 조회된 사용자가 있을 경우
     * - 2.1. 접근한 email 과 조회된 email 이 다를 경우 (소셜의 이메일을 변경했을 때 발생)
     * --- 2.1.1 변경한 메일이 이미 존재할 경우 -> 사용중인 이메일로 변경했으므로 재변경 요청
     * --- 2.1.2 변경한 메일이 존재하지 않을 경우 -> 사용자의 메일을 변경하고 사용자 정보 업데이트
     * - 2.2. 접근한 email 과 조회된 email 이 같을 경우 -> 사용자 정보 반환
     */
    @Transactional
    fun loadOrCreateUser(oAuth2User: OAuth2AuthenticationToken): Users {
        val (providerType, providerId, email) = getOauthInfo(oAuth2User)

        val user = userRepository.findByProviderTypeAndProviderId(providerType, providerId)

        return if (user == null) {
            processWhenUserIsNull(providerType, providerId, email)
        } else {
            processWhenUserIsNotNull(user, email)
        }
    }

    private fun processWhenUserIsNull(
        providerType: ProviderType,
        providerId: String,
        email: String
    ): Users {
        val user = userRepository.findByEmail(email)

        if (user != null) {
            throw CustomException(ErrorType.CANNOT_CHANGE_EMAIL, DomainCode.COMMON)
        }

        return userRepository.save(
            Users(
                email = email,
                providerType = providerType,
                providerId = providerId
            )
        )
    }

    private fun processWhenUserIsNotNull(users: Users, email: String): Users {
        // 접근한 email 과 조회된 email 이 다를 경우
        if (users.email != email) {
            val userByEmail = userRepository.findByEmail(email)

            if (userByEmail != null) {
                throw CustomException(ErrorType.ALREADY_EXIST_EMAIL, DomainCode.COMMON)
            }

            users.email = email
            return userRepository.save(users)
        }

        return users
    }

    private fun getOauthInfo(oAuth2User: OAuth2AuthenticationToken): Triple<ProviderType, String, String> {
        val providerType = getProviderType(oAuth2User)
        val providerId = getProviderId(oAuth2User.principal, providerType)
        val email = getEmail(oAuth2User.principal, providerType)

        return Triple(providerType, providerId, email)
    }

    private fun getEmail(oAuth2User: OAuth2User, providerType: ProviderType): String {
        if (providerType == ProviderType.GOOGLE) {
            return oAuth2User.getAttribute("email") as? String
                ?: throw CustomException(
                    ErrorType.INVALID_OAUTH2_PROVIDER,
                    DomainCode.COMMON
                )
        }

        throw CustomException(ErrorType.INVALID_OAUTH2_PROVIDER, DomainCode.COMMON)
    }

    private fun getProviderId(oAuth2User: OAuth2User, providerType: ProviderType): String {
        if (providerType == ProviderType.GOOGLE) {
            return oAuth2User.getAttribute("sub") as? String
                ?: throw CustomException(ErrorType.INVALID_OAUTH2_PROVIDER, DomainCode.COMMON)
        }

        throw CustomException(ErrorType.INVALID_OAUTH2_PROVIDER, DomainCode.COMMON)
    }

    private fun getProviderType(oAuth2User: OAuth2AuthenticationToken): ProviderType {
        val provider = oAuth2User.authorizedClientRegistrationId.uppercase()

        return ProviderType.entries.stream()
            .filter { it.name == provider }
            .findFirst()
            .orElseThrow {
                throw CustomException(ErrorType.INVALID_OAUTH2_PROVIDER, DomainCode.COMMON)
            }
    }
}
