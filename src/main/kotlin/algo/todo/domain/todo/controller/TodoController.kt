package algo.todo.domain.todo.controller

import ApiResponse
import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.controller.dto.request.TodoPageRequest
import algo.todo.domain.todo.controller.dto.request.UpdateTodoRequest
import algo.todo.domain.todo.controller.dto.response.TodoDetailResponse
import algo.todo.domain.todo.controller.dto.response.TodoListResponse
import algo.todo.domain.todo.service.TodoService
import algo.todo.global.constant.ApiEndpointV1
import algo.todo.global.security.CustomUserDetails
import jakarta.validation.Valid
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
            userId = userDetails.users.id,
            todoPageRequest = pageRequest
        )

        val todoListResponse = todos.get().map {
            TodoListResponse.from(it).applyPreview(pageRequest.parsePreview())
        }.toList()

        return ResponseEntity.ok().body(
            ApiResponse.successList(todos.totalPages, pageRequest.base.page, todoListResponse)
        )
    }

    @GetMapping(ApiEndpointV1.TODO + "/{id}")
    fun getTodoDetail(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<TodoDetailResponse>> {
        val todo = todoService.getTodoDetail(
            userId = userDetails.users.id,
            todoId = id
        )

        return ResponseEntity.ok().body(ApiResponse.success(TodoDetailResponse.from(todo)))
    }

    @PostMapping(ApiEndpointV1.TODO)
    fun createTodos(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody requestDto: CreateTodoRequest
    ): ResponseEntity<ApiResponse<Long>> {
        val savedTodo = todoService.createTodo(
            userId = userDetails.users.id,
            requestDto = requestDto
        )

        return ResponseEntity.ok().body(ApiResponse.success(savedTodo.id))
    }

    @PatchMapping(ApiEndpointV1.TODO + "/{id}")
    fun updateTodo(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
        @Valid @RequestBody requestDto: UpdateTodoRequest
    ): ResponseEntity<ApiResponse<Void>> {
        todoService.updateTodo(
            userId = userDetails.users.id,
            todoId = id,
            requestDto = requestDto
        )

        return ResponseEntity.ok().body(ApiResponse.success(null))
    }

    @DeleteMapping(ApiEndpointV1.TODO + "/{id}")
    fun deleteTodo(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Void>> {
        todoService.deleteTodo(
            userId = userDetails.users.id,
            todoId = id
        )

        return ResponseEntity.ok().body(ApiResponse.success(null))
    }

}
