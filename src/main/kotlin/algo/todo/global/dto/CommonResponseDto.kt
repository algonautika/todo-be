package algo.todo.global.dto

import algo.todo.global.exception.CustomException
import org.springframework.http.HttpStatus

class CommonResponseDto private constructor(
    val status: String,
    val message: String,
    val data: Any?
) {
    constructor(
        domainCode: DomainCode,
        status: HttpStatus,
        message: String,
        data: Any?
    ) : this(
        "${status.value()}-${domainCode.code}",
        message,
        data
    )

    constructor(
        exception: CustomException
    ) : this(
        "${exception.errorType.status}-${exception.domainCode.code}",
        exception.errorType.message,
        null
    )
}
