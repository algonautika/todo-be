package algo.todo.domain.user.entity

import algo.todo.domain.auth.entity.RefreshToken
import algo.todo.domain.todo.entity.Todo
import algo.todo.global.security.ProviderType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.envers.Audited
import java.time.LocalDateTime

@Audited
@Entity
@Table(name = "users")
class Users private constructor(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @field:Column(name = "email", columnDefinition = "varchar(100) NOT NULL", nullable = false)
    var email: String,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "provider", columnDefinition = "varchar(20) NOT NULL", nullable = false)
    val providerType: ProviderType,

    @field:Column(name = "provider_id", columnDefinition = "varchar(50) NOT NULL", nullable = false)
    val providerId: String,

    @field:CreationTimestamp
    @field:Column(name = "created_at", columnDefinition = "TIMESTAMP NOT NULL", nullable = false)
    val createdAt: LocalDateTime,

    @field:OneToOne(mappedBy = "users", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var refreshToken: RefreshToken? = null,

    @field:OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var todos: MutableList<Todo> = mutableListOf(),
) {
    constructor(
        email: String,
        providerType: ProviderType,
        providerId: String,
    ) : this(
        id = 0,
        email = email,
        providerType = providerType,
        providerId = providerId,
        createdAt = LocalDateTime.now(),
    )
}
