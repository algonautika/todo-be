package algo.todo.global.exception

import algo.todo.global.dto.ApiErrorResponse
import algo.todo.global.dto.DomainCode
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

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val log = LogManager.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiErrorResponse> {
        printErrorMessage(e)

        val dto = ApiErrorResponse(
            errorType = ErrorType.UNCAUGHT_EXCEPTION,
            domainCode = DomainCode.COMMON
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(dto)
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiErrorResponse> {
        printErrorMessage(e)
        val dto = ApiErrorResponse(
            exception = e
        )
        return ResponseEntity
            .status(e.errorType.status)
            .body(dto)
    }

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val apiErrorResponse = ApiErrorResponse(
            ErrorType.NOT_FOUND,
            DomainCode.COMMON
        )
        return ResponseEntity(apiErrorResponse, HttpStatus.NOT_FOUND)
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
