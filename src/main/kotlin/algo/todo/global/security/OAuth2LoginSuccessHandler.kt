package algo.todo.global.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class OAuth2LoginSuccessHandler(
//    private val userService: UserService,
//    private val jwtService: JwtService
) : AuthenticationSuccessHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // (1) OAuth2User 정보 가져오기
//        val oAuth2User = authentication.principal as OAuth2User
//        val email = oAuth2User.getAttribute<String>("email")
//        // (2) DB에 사용자 등록 or 조회
//        val user = userService.loadOrCreateUser(email)
//        // (3) JWT 생성
//        val jwt = jwtService.generateToken(user)
        // (4) JWT를 쿠키에 담아 클라이언트에 반환
        val cookie = Cookie("access_token", "test_token")
        cookie.path = "/"
        cookie.isHttpOnly = true
        response.addCookie(cookie)
        response.sendRedirect("http://localhost:5173")
    }
}
