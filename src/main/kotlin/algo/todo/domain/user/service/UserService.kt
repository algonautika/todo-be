package algo.todo.domain.user.service

import algo.todo.domain.user.entity.User
import algo.todo.domain.user.repository.UserRepository
import algo.todo.global.security.ProviderType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun loadOrCreateUser(attribute: Map<String, Any>, providerType: ProviderType): User? {

        return null

    }

//    private fun loadUser(attribute: Map<String, Any>, provider: Provider): User {
//        if (provider == Provider.GOOGLE) {
//
//        }
//
//        throw
//    }
}
