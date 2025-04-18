package algo.todo.domain.auth.controller

import algo.todo.domain.auth.service.AuthService
import algo.todo.global.constant.ApiEndpointV1
import algo.todo.global.util.CookieUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping(ApiEndpointV1.AUTH + "/refresh")
    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @CookieValue(name = "refresh_token") refreshToken: String
    ): ResponseEntity<Void> {
        val reIssueTokens = authService.reIssueToken(refreshToken)

        CookieUtil.setAccessTokenAndRefreshTokenCookie(
            response = response,
            accessToken = reIssueTokens.accessToken,
            refreshToken = reIssueTokens.refreshToken,
        )

        return ResponseEntity.ok().build()
    }
}
