package algo.todo.domain.todo.controller

import ApiResponse
import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.service.TodoService
import algo.todo.global.constant.ApiEndpointV1
import algo.todo.global.security.CustomUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoController(
    private val todoService: TodoService
) {
    @GetMapping(ApiEndpointV1.TODO)
    fun getTodos(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse> {
        val todos = todoService.getTodos(userDetails.users.id)
        return ResponseEntity.ok().body(ApiResponse.success(todos))
    }

    @PostMapping(ApiEndpointV1.TODO)
    fun createTodos(@RequestBody requestDto: CreateTodoRequest): ResponseEntity<ApiResponse> {
        val savedTodo = todoService.createTodo(requestDto)
        return ResponseEntity.ok().body(ApiResponse.success(savedTodo.id))
    }

}
