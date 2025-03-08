package algo.todo.global.dto

import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType

class ApiErrorResponse private constructor(
    val status: String,
    val message: String
) {
    constructor(
        errorType: ErrorType,
        domainCode: DomainCode
    ) : this(
        "${errorType.status.value()}-${domainCode.code}",
        errorType.message,
    )

    constructor(exception: CustomException) : this(
        "${exception.errorType.status.value()}-${exception.domainCode.code}",
        exception.errorType.message
    )
}
