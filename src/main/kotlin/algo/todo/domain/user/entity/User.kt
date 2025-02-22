package algo.todo.domain.user.entity

import algo.todo.global.security.ProviderType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_user")
class User private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "email")
    var email: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    val providerType: ProviderType,

    @Column(name = "provider_id")
    val providerId: String,

    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime
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

