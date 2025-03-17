package algo.todo.domain.todo.controller

import ApiResponse
import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.entity.Todo
import algo.todo.domain.todo.service.TodoService
import algo.todo.global.constant.ApiEndpointV1
import algo.todo.global.security.CustomUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class TodoController(
    private val todoService: TodoService
) {
    @GetMapping(ApiEndpointV1.TODO)
    fun getTodos(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Long,
        @RequestParam(name = "sort", defaultValue = "createAt:desc") sort: String,
        @RequestParam(name = "preview", defaultValue = "description:500") preview: String,
    ): ResponseEntity<ApiResponse<Todo>> {
        val todos = todoService.getTodosWithPagination(
            userDetails.users.id
        )
        return ResponseEntity.ok().body(ApiResponse.successList(0, 0, todos))
    }

    @PostMapping(ApiEndpointV1.TODO)
    fun createTodos(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestBody requestDto: CreateTodoRequest
    ): ResponseEntity<ApiResponse<Long>> {
        val savedTodo = todoService.createTodo(userDetails.users.id, requestDto)
        return ResponseEntity.ok().body(ApiResponse.success(savedTodo.id))
    }

}
