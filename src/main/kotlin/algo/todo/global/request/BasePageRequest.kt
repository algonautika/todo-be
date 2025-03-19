package algo.todo.global.request

data class BasePageRequest(
    val page: Int = 0,
    val pageSize: Int = 10,
)
