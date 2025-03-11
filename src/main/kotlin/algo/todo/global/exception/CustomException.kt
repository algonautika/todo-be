package algo.todo.global.exception

import algo.todo.global.response.DomainCode

data class CustomException(
    val errorType: ErrorType,
    val domainCode: DomainCode,
) : RuntimeException()
