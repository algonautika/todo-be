package algo.todo.domain.user.repository

import algo.todo.domain.user.entity.User
import algo.todo.global.security.ProviderType
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByProviderTypeAndProviderId(providerType: ProviderType, providerId: String): User?
    fun findByEmail(email: String): User?
}
