package algo.todo.global.exception

import algo.todo.global.dto.CommonResponseDto
import algo.todo.global.dto.DomainCode
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.function.EntityResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object {
        private val log = LogManager.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): EntityResponse<CommonResponseDto> {
        printErrorMessage(e)

        val dto = CommonResponseDto(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            domainCode = DomainCode.COMMON,
            message = "Uncaught Exception",
            data = null
        )
        return EntityResponse
            .fromObject(dto)
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build()
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): EntityResponse<CommonResponseDto> {
        printErrorMessage(e)
        val dto = CommonResponseDto(
            exception = e
        )
        return EntityResponse
            .fromObject(dto)
            .status(e.errorType.status)
            .build()
    }

    /**
     * Exception 객체로부터 에러 메시지를 추출하는 메소드
     */
    private fun printErrorMessage(e: Exception) {
        when (e) {
            is NullPointerException -> log.error("NullPointerException: ${e.message}")
            is CustomException -> log.warn("CustomException: ${e.message}")
            else -> log.error("Uncaught Exception: ${e.message}")
        }
    }

}
