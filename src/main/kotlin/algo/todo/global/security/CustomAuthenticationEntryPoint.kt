package algo.todo.global.security

import algo.todo.global.dto.DomainCode
import algo.todo.global.exception.ErrorType
import algo.todo.global.util.ExceptionUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

/**
 * 401 Unauthorized 에러를 처리하는 커스텀 AuthenticationEntryPoint
 */
@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    companion object {
        private val log = LogManager.getLogger(CustomAuthenticationEntryPoint::class.java)
    }

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.warn("Unauthorized 401 error: {}", authException.message)

        ExceptionUtil.writeErrorJson(
            response,
            ErrorType.UNAUTHORIZED,
            DomainCode.COMMON
        )
    }
}
