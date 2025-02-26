package algo.todo.domain.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class ReIssueRequestDto(

    @NotBlank(message = "refreshToken 은 필수 입력 값입니다.")
    val refreshToken: String
)
