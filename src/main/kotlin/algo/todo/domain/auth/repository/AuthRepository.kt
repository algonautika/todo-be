package algo.todo.domain.auth.repository

import algo.todo.domain.auth.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface AuthRepository : JpaRepository<RefreshToken, Long> {
    fun existsByUsersIdAndRefreshToken(
        usersId: Long,
        refreshToken: String
    ): Boolean
}
