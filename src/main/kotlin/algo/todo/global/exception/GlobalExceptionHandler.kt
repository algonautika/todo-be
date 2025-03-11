package algo.todo.global.exception

import ApiResponse
import algo.todo.global.response.DomainCode
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * REST 컨트롤러에서 발생하는 예외를 전역적으로 처리하는 핸들러 클래스
 *
 * `@RestControllerAdvice`를 통해 스프링 컨텍스트 내 모든 컨트롤러에서 발생하는
 * 예외를 이 핸들러로 모아, 일관된 에러 응답을 반환
 */
@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    companion object {
        private val log = LogManager.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse> {
        printErrorMessage(e)

        val response =
            ApiResponse.error(
                errorType = ErrorType.UNCAUGHT_EXCEPTION,
                domainCode = DomainCode.COMMON,
            )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiResponse> {
        printErrorMessage(e)
        val response =
            ApiResponse.error(
                exception = e,
            )
        return ResponseEntity
            .status(e.errorType.status)
            .body(response)
    }

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val response =
            ApiResponse.error(
                ErrorType.NOT_FOUND,
                DomainCode.COMMON,
            )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
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
