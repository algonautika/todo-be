package algo.todo.global.exception

import algo.todo.global.dto.DomainCode

data class CustomException(
    val errorType: ErrorType,
    val domainCode: DomainCode
) : RuntimeException()
