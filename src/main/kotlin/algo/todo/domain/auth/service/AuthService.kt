package algo.todo.domain.auth.service

import algo.todo.domain.auth.repository.AuthRepository
import algo.todo.global.security.JwtProvider
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authRepository: AuthRepository,
    private val jwtProvider: JwtProvider
) {
    fun reIssueToken(refreshToken: String) {
        // 1. refreshToken 조회

        // 2. refreshToken 만료 여부 확인
        jwtProvider.ensureValidToken(refreshToken)

        // 3. accessToken 재발급

        // 4. refreshToken 재발급
    }

}
