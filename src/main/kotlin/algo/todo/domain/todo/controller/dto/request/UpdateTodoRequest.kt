package algo.todo.domain.todo.controller.dto.request

import algo.todo.domain.todo.validate.ValidTimeZone
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class UpdateTodoRequest(
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
    val timeZone: String
)
