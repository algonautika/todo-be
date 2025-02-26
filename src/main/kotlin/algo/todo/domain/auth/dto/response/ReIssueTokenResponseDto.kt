package algo.todo.domain.auth.dto.response

data class ReIssueTokenResponseDto(
    val accessToken: String,
    val refreshToken: String
)

