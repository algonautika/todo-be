package algo.todo.domain.todo.controller.dto.request

import algo.todo.domain.todo.entity.Todo
import algo.todo.domain.todo.validate.ValidTimeZone
import algo.todo.domain.user.entity.Users
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CreateTodoRequest(
    @field:NotBlank(message = "title is required")
    val title: String,
    @field:NotBlank(message = "description is required")
    val description: String,
    @field:NotBlank(message = "content is required")
    val startDate: LocalDateTime,
    @field:NotBlank(message = "content is required")
    val endDate: LocalDateTime,
    @field:NotBlank(message = "content is required")
    val deadline: LocalDateTime,
    @field:NotBlank(message = "content is required")
    @field:ValidTimeZone(message = "timeZone is invalid or not supported.")
    val timeZone: String,
) {
    fun toEntity(users: Users): Todo {
        return Todo(
            user = users,
            title = title,
            description = description,
            startDate = startDate,
            endDate = endDate,
            deadline = deadline,
            timeZone = timeZone,
        )
    }
}
