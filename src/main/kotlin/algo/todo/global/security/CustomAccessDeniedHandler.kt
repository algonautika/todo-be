package algo.todo.global.security

import algo.todo.global.dto.DomainCode
import algo.todo.global.exception.ErrorType
import algo.todo.global.util.ExceptionUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    companion object {
        private val log =
            LogManager.getLogger(CustomAccessDeniedHandler::class.java)
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        log.warn("Forbidden 403 error: {}", accessDeniedException.message)

        ExceptionUtil.writeErrorJson(
            response,
            ErrorType.FORBIDDEN,
            DomainCode.COMMON
        )
    }
}
