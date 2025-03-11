package algo.todo.global.response

class ApiSuccessListResponse(
    val pageSize: Int,
    val page: Int,
    val data: List<Any>,
)
