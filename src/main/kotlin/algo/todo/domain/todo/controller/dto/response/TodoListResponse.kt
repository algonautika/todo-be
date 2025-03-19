package algo.todo.domain.todo.controller.dto.response

import algo.todo.domain.todo.entity.Todo
import java.time.LocalDateTime
import java.util.*

data class TodoListResponse(
    val id: Long,
    val title: String,
    val userId: Long,
    val description: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val deadline: LocalDateTime,
    val createdAt: LocalDateTime,
    val timeZone: TimeZone,
) {
    constructor(todo: Todo, previewSize: Int) : this(
        id = todo.id,
        title = todo.title,
        userId = todo.user.id,
        description = todo.description.take(previewSize),
        startDate = todo.startDate,
        endDate = todo.endDate,
        deadline = todo.deadline,
        createdAt = todo.createdAt,
        timeZone = todo.timeZone,
    )
}
