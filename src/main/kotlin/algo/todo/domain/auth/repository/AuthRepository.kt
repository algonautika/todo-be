package algo.todo.domain.auth.repository

import algo.todo.domain.auth.entity.RefreshToken
import algo.todo.domain.user.entity.Users
import org.springframework.data.jpa.repository.JpaRepository

interface AuthRepository : JpaRepository<RefreshToken, Long> {
    fun findByRefreshToken(refreshToken: String): RefreshToken?

    fun findByUsers(users: Users): RefreshToken?
}
