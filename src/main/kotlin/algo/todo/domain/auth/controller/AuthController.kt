package algo.todo.domain.auth.controller

import algo.todo.domain.auth.controller.dto.request.ReIssueRequestDto
import algo.todo.domain.auth.service.AuthService
import algo.todo.global.dto.ApiSuccessResponse
import algo.todo.global.util.CookieUtil
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody @Valid requestDto: ReIssueRequestDto, response: HttpServletResponse)
            : ResponseEntity<ApiSuccessResponse> {
        val reIssueToken = authService.reIssueToken(requestDto)

        CookieUtil.setAccessTokenAndRefreshTokenCookie(
            response = response,
            accessToken = reIssueToken.accessToken,
            refreshToken = reIssueToken.refreshToken
        )

        return ResponseEntity.ok().body(
            ApiSuccessResponse(
                data = reIssueToken
            )
        )
    }
}
