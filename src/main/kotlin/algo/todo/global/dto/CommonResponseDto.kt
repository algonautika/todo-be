package algo.todo.global.dto

import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType

class CommonResponseDto private constructor(
    val status: String,
    val message: String,
    val data: Any?
) {
    constructor(
        errorType: ErrorType,
        domainCode: DomainCode,
        data: Any?
    ) : this(
        "${errorType.status.value()}-${domainCode.code}",
        errorType.message,
        data
    )

    constructor(exception: CustomException) : this(
        "${exception.errorType.status.value()}-${exception.domainCode.code}",
        exception.errorType.message,
        null
    )
}
