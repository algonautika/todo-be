package algo.todo.domain.auth.entity

import algo.todo.domain.user.entity.Users
import jakarta.persistence.*

@Entity
@Table(name = "refresh_token")
class RefreshToken private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val users: Users,

    @Column(name = "refresh_token")
    var refreshToken: String,
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
