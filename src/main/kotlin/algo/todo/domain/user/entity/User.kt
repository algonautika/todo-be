package algo.todo.domain.user.entity

import algo.todo.global.security.ProviderType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "name")
    val name: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "provider")
    val providerType: ProviderType,

    @Column(name = "provider_id")
    val providerId: String,

    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime
)

