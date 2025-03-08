package algo.todo.domain.user.controller

import algo.todo.domain.user.controller.dto.UserProfileResponse
import algo.todo.global.constant.ApiEndpointV1
import algo.todo.global.dto.ApiSuccessResponse
import algo.todo.global.security.CustomUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @GetMapping(ApiEndpointV1.USER + "/me")
    fun getUser(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<ApiSuccessResponse> {
        val userProfile = UserProfileResponse(userDetails.users)
        return ResponseEntity.ok(ApiSuccessResponse(userProfile));
    }
}
