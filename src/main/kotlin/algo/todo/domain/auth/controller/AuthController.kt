package algo.todo.domain.auth.controller

import algo.todo.domain.auth.dto.request.ReIssueRequestDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController {

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody @Valid refreshToken: ReIssueRequestDto) {


    }
}
