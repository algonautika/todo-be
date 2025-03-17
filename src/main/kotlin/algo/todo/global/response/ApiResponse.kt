import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode

/**
 * API 의 일관된 응답을 주기 위한 클래스
 */
sealed class ApiResponse<T> {
    data class Success<T>(val data: T?) : ApiResponse<T>()
    data class SuccessList<T>(val pageSize: Int, val page: Int, val data: List<T>) : ApiResponse<T>()
    data class Error<T>(val status: String, val message: String) : ApiResponse<T>()

    companion object {
        fun <T> success(data: T?) = Success(data)

        fun <T> successList(
            pageSize: Int,
            page: Int,
            data: List<T>,
        ) = SuccessList(pageSize, page, data)

        fun <T> error(
            errorType: ErrorType,
            domainCode: DomainCode,
        ): Error<T> {
            val status = "${errorType.status.value()}-${domainCode.code}"
            return Error(status, errorType.message)
        }

        fun <T> error(exception: CustomException): Error<T> {
            val status = "${exception.errorType.status.value()}-${exception.domainCode.code}"
            return Error(status, exception.errorType.message)
        }
    }
}
