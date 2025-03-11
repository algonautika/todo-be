package algo.todo.global.security

import algo.todo.domain.auth.service.AuthService
import algo.todo.domain.user.service.UserService
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.exception.FailureHandler
import algo.todo.global.response.DomainCode
import algo.todo.global.util.CookieUtil
import jakarta.persistence.EntityManager
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OAuth2LoginSuccessHandler(
    private val entityManager: EntityManager,
    private val userService: UserService,
    private val jwtProvider: JwtProvider,
    private val authService: AuthService,
) : AuthenticationSuccessHandler {
    /**
     * OAuth2 로그인 성공 시 호출되는 메서드
     * 따라서 security 내부적으로 인증을 보장하기 때문에 Authentication 데이터를 신뢰할 수 있음
     */
    @Transactional
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        runCatching {
            val oAuth2User = getOauth2AuthenticationTokenFromAuthentication(authentication)

            val user = userService.loadOrCreateUser(oAuth2User)

            val accessToken = jwtProvider.generateAccessToken(user)
            val refreshToken = jwtProvider.generateRefreshToken(user)

            CookieUtil.setAccessTokenAndRefreshTokenCookie(
                response = response,
                accessToken = accessToken,
                refreshToken = refreshToken,
            )

            authService.upsertRefreshToken(user, refreshToken)

            response.sendRedirect("http://localhost:5173")
        }.onFailure {
            FailureHandler.handleFailure(it, response)
        }
    }

    private fun getOauth2AuthenticationTokenFromAuthentication(authentication: Authentication): OAuth2AuthenticationToken =
        authentication as? OAuth2AuthenticationToken ?: throw CustomException(
            ErrorType.INVALID_OAUTH2_PROVIDER,
            DomainCode.COMMON,
        )
}
