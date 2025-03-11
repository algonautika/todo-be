package algo.todo.domain.auth.controller.dto.response

data class ReIssueTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
