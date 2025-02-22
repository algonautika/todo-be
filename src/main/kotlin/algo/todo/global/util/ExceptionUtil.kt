package algo.todo.global.util

import algo.todo.global.dto.CommonResponseDto
import algo.todo.global.dto.DomainCode
import algo.todo.global.exception.ErrorType
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.nio.charset.StandardCharsets

class ExceptionUtil {

    companion object {
        fun writeErrorJson(
            response: HttpServletResponse,
            errorType: ErrorType,
            domainCode: DomainCode
        ) {
            val dto = CommonResponseDto(
                errorType = errorType,
                domainCode = domainCode,
                data = null
            )

            val json = ObjectMapper().writeValueAsString(dto)

            response.contentType = APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.status = errorType.status.value()

            response.writer.write(json)
            response.writer.flush()
            response.writer.close()
        }
    }

}
