package algo.todo.domain.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class ReIssueRequestDto(

    @NotBlank
    val refreshToken: String
)
