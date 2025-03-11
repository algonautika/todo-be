package algo.todo.global.exception

import org.springframework.http.HttpStatus

enum class ErrorType(
    val status: HttpStatus,
    val message: String,
) {
    UNCAUGHT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Uncaught exception"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),
    INVALID_OAUTH2_PROVIDER(HttpStatus.BAD_REQUEST, "Invalid OAuth2 provider"),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "Already exist email"),
    CANNOT_CHANGE_EMAIL(HttpStatus.BAD_REQUEST, "Cannot change to an email that is already in use"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "Invalid token"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),
}
