package algo.todo.domain.auth.entity

import algo.todo.domain.user.entity.Users
import jakarta.persistence.*

@Entity
@Table(name = "refresh_token")
class RefreshToken private constructor(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @field:OneToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id")
    var users: Users,

    @field:Column(name = "refresh_token")
    var refreshToken: String
) {
    constructor(
        users: Users,
        refreshToken: String
    ) : this(
        id = 0,
        users = users,
        refreshToken = refreshToken
    )

    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }
}
