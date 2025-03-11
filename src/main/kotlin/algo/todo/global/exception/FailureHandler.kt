package algo.todo.global.exception

import algo.todo.global.response.DomainCode
import algo.todo.global.util.ExceptionUtil
import jakarta.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager

/**
 * Global Exception Handler 를 통해 처리되지 않은 예외를 처리하는 클래스
 */
object FailureHandler {
    private val log = LogManager.getLogger(FailureHandler::class.java)

    fun handleFailure(
        throwable: Throwable,
        response: HttpServletResponse,
    ) {
        when (throwable) {
            is CustomException -> {
                ExceptionUtil.writeErrorJson(
                    response,
                    throwable.errorType,
                    throwable.domainCode,
                )
                throw throwable
            }

            else -> {
                log.error(throwable.stackTraceToString())
                ExceptionUtil.writeErrorJson(
                    response,
                    ErrorType.UNCAUGHT_EXCEPTION,
                    DomainCode.USER,
                )
                throw throwable
            }
        }
    }
}
