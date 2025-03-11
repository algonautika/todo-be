import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode

/**
 * API 의 일관된 응답을 주기 위한 클래스
 */
sealed class ApiResponse {
    data class Success(val data: Any?) : ApiResponse()
    data class SuccessList(val pageSize: Int, val page: Int, val data: List<Any>) : ApiResponse()
    data class Error(val status: String, val message: String) : ApiResponse()

    companion object {
        fun success(data: Any?) = Success(data)

        fun successList(
            pageSize: Int,
            page: Int,
            data: List<Any>,
        ) = SuccessList(pageSize, page, data)

        fun error(
            errorType: ErrorType,
            domainCode: DomainCode,
        ): Error {
            val status = "${errorType.status.value()}-${domainCode.code}"
            return Error(status, errorType.message)
        }

        fun error(exception: CustomException): Error {
            val status = "${exception.errorType.status.value()}-${exception.domainCode.code}"
            return Error(status, exception.errorType.message)
        }
    }
}
