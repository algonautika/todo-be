package algo.todo.domain.auth.controller.dto.response

data class ReIssueTokenResponseDto(
    val accessToken: String,
    val refreshToken: String
)

