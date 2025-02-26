package algo.todo.global.exception

import algo.todo.global.dto.CommonResponseDto
import algo.todo.global.dto.DomainCode
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val log = LogManager.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<CommonResponseDto> {
        printErrorMessage(e)

        val dto = CommonResponseDto(
            errorType = ErrorType.UNCAUGHT_EXCEPTION,
            domainCode = DomainCode.COMMON,
            data = null
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(dto)
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<CommonResponseDto> {
        printErrorMessage(e)
        val dto = CommonResponseDto(
            exception = e
        )
        return ResponseEntity
            .status(e.errorType.status)
            .body(dto)
    }

    /**
     * Exception 객체로부터 에러 메시지를 추출하는 메소드
     */
    private fun printErrorMessage(e: Exception) {
        when (e) {
            is NullPointerException -> log.error(e.stackTraceToString())
            is CustomException -> {} // do nothing
            else -> log.error(e.stackTraceToString())
        }
    }

}
