package algo.todo.domain.todo.controller.dto.response

import algo.todo.domain.todo.entity.Todo
import java.time.LocalDateTime
import java.util.*

data class TodoListResponse(
    val id: Long,
    var title: String,
    val userId: Long,
    var description: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val deadline: LocalDateTime,
    val createdAt: LocalDateTime,
    val timeZone: TimeZone,
) {
    constructor(todo: Todo) : this(
        id = todo.id,
        title = todo.title,
        userId = todo.user.id,
        description = todo.description,
        startDate = todo.startDate,
        endDate = todo.endDate,
        deadline = todo.deadline,
        createdAt = todo.createdAt,
        timeZone = todo.timeZone,
    )

    fun applyPreview(preview: Pair<String, Int>): TodoListResponse {
        val defaultTakeLength = 500
        val takeLength = preview.second + 1

        return when (preview.first) {
            "title" -> this.copy(title = this.title.take(takeLength))
            "description" -> this.copy(description = this.description.take(takeLength))
            else -> this.copy(description = this.description.take(defaultTakeLength))
        }
    }
}
