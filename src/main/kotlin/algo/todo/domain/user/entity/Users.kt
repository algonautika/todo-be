package algo.todo.domain.user.entity

import algo.todo.domain.auth.entity.RefreshToken
import algo.todo.global.security.ProviderType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class Users private constructor(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @field:Column(name = "email")
    var email: String,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "provider")
    val providerType: ProviderType,

    @field:Column(name = "provider_id")
    val providerId: String,

    @field:CreationTimestamp
    @field:Column(name = "created_at")
    val createdAt: LocalDateTime,

    @field:OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @field:JoinColumn(name = "refresh_token_id")
    var refreshToken: RefreshToken? = null
) {
    constructor(
        email: String,
        providerType: ProviderType,
        providerId: String
    ) : this(
        id = 0,
        email = email,
        providerType = providerType,
        providerId = providerId,
        createdAt = LocalDateTime.now()
    )
}

