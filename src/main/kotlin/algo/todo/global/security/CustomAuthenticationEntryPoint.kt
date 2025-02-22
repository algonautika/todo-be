package algo.todo.global.security

import algo.todo.global.dto.CommonResponseDto
import algo.todo.global.dto.DomainCode
import algo.todo.global.util.JsonUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

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
        log.debug("Unauthorized 401 error: {}", authException.message)

        val dto = CommonResponseDto(
            status = HttpStatus.UNAUTHORIZED,
            domainCode = DomainCode.COMMON,
            message = "Unauthorized",
            data = null
        )

        val jsonBody = JsonUtil.toJsonString(dto)

        response.contentType = APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        response.writer.write(jsonBody)
        response.writer.flush()
        response.writer.close()
    }
}
