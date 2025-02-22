package algo.todo.global.security

import algo.todo.global.dto.CommonResponseDto
import algo.todo.global.dto.DomainCode
import algo.todo.global.util.JsonUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    companion object {
        private val log = LogManager.getLogger(CustomAccessDeniedHandler::class.java)
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        log.debug("Forbidden 403 error: {}", accessDeniedException.message)

        val dto = CommonResponseDto(
            status = HttpStatus.FORBIDDEN,
            domainCode = DomainCode.COMMON,
            message = "Forbidden",
            data = null
        )

        val jsonBody = JsonUtil.toJsonString(dto)

        response.contentType = APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.status = HttpServletResponse.SC_FORBIDDEN

        response.writer.write(jsonBody)
        response.writer.flush()
        response.writer.close()
    }
}
