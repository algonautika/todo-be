package algo.todo.domain.auth.service

import algo.todo.domain.auth.controller.dto.request.ReIssueRequestDto
import algo.todo.domain.auth.controller.dto.response.ReIssueTokenResponseDto
import algo.todo.domain.auth.entity.RefreshToken
import algo.todo.domain.auth.repository.AuthRepository
import algo.todo.domain.user.entity.Users
import algo.todo.global.dto.DomainCode
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.security.JwtProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val authRepository: AuthRepository,
    private val jwtProvider: JwtProvider
) {

    @Transactional
    fun reIssueToken(requestDto: ReIssueRequestDto): ReIssueTokenResponseDto {
        // 1. refreshToken 조회
        val findRefreshToken = authRepository.findByRefreshToken(requestDto.refreshToken)
            ?: throw CustomException(ErrorType.INVALID_TOKEN, DomainCode.COMMON)

        // 2. refreshToken 유효성 확인
        jwtProvider.ensureValidToken(requestDto.refreshToken)

        // 3. accessToken & refreshToken 재발급
        val accessToken = jwtProvider.generateAccessToken(findRefreshToken.users)
        val newRefreshToken = jwtProvider.generateRefreshToken(findRefreshToken.users)

        // 4. refreshToken 업데이트
        findRefreshToken.updateRefreshToken(newRefreshToken)

        return ReIssueTokenResponseDto(
            accessToken = accessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun upsertRefreshToken(users: Users, refreshToken: String) {
        authRepository.findByUsers(users)
            ?.updateRefreshToken(refreshToken)
            ?: authRepository.save(
                RefreshToken(
                    users = users,
                    refreshToken = refreshToken
                )
            )
    }

}
