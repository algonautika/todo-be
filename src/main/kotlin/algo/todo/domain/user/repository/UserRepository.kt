package algo.todo.domain.user.repository

import algo.todo.domain.user.entity.Users
import algo.todo.global.security.ProviderType
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<Users, Long> {
    fun findByProviderTypeAndProviderId(providerType: ProviderType, providerId: String): Users?
    fun findByEmail(email: String): Users?
    fun findByIdAndEmail(id: Long, email: String): Users?
}
