package algo.todo.domain.user.controller.dto

import algo.todo.domain.user.entity.Users

data class UserProfileResponse(
    val id: Long,
    val email: String
) {
    constructor(user: Users) : this(
        id = user.id,
        email = user.email
    )
}
