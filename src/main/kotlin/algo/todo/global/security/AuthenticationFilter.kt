package algo.todo.global.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter


class AuthenticationFilter(
    private val tokenProvider: JwtProvider
) : OncePerRequestFilter() {

    companion object {
        const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        getAccessTokenFromRequest(request)?.let { token ->
            tokenProvider.getAuthentication(token)
                ?.let {
                    val context = SecurityContextHolder.getContext()
                    context.authentication = it
                }
        }

        filterChain.doFilter(request, response)
    }

    private fun getAccessTokenFromRequest(
        request: HttpServletRequest
    ): String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        ?.takeIf { it.isNotBlank() && it.startsWith(BEARER_PREFIX) }
        ?.removePrefix(BEARER_PREFIX)
}
