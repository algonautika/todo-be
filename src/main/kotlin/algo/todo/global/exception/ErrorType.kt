package algo.todo.global.exception

import org.springframework.http.HttpStatus

enum class ErrorType(
    val status: HttpStatus,
    val message: String
) {
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "User not found")
}
