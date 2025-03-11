package algo.todo.global.util

import ApiResponse
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.nio.charset.StandardCharsets

class ExceptionUtil {
    companion object {
        fun writeErrorJson(
            response: HttpServletResponse,
            errorType: ErrorType,
            domainCode: DomainCode,
        ) {
            val errorResponse = ApiResponse.error(
                errorType = errorType,
                domainCode = domainCode,
            )

            val json = ObjectMapper().writeValueAsString(errorResponse)

            response.contentType = APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.status = errorType.status.value()

            response.writer.write(json)
            response.writer.flush()
            response.writer.close()
        }
    }
}
