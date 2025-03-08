package algo.todo.global.security

import algo.todo.global.util.CookieUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter


class AuthenticationFilter(
    private val tokenProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        CookieUtil.getAccessTokenFromCookie(request).onSuccess { token ->
            tokenProvider.getAuthentication(token, response).onSuccess { auth ->
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }

}
