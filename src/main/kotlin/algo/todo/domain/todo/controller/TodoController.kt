package algo.todo.domain.todo.controller

import ApiResponse
import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.controller.dto.request.TodoPageRequest
import algo.todo.domain.todo.controller.dto.response.TodoListResponse
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
        @ModelAttribute pageRequest: TodoPageRequest
    ): ResponseEntity<ApiResponse<TodoListResponse>> {
        val todos = todoService.getTodosWithPagination(
            userDetails.users.id,
            pageRequest
        )

        val todoListResponse = todos.get().map {
            TodoListResponse(it).applyPreview(pageRequest.parsePreview())
        }.toList()

        return ResponseEntity.ok().body(
            ApiResponse.successList(todos.totalPages, pageRequest.base.page, todoListResponse)
        )
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
