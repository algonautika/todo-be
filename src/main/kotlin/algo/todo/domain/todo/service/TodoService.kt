package algo.todo.domain.todo.service

import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.entity.Todo
import algo.todo.domain.todo.repository.TodoRepository
import algo.todo.domain.user.service.UserService
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val userService: UserService,
    private val todoRepository: TodoRepository
) {
    fun getTodosWithPagination(userId: Long): List<Todo> {
        return todoRepository.findAllByUserId(userId)
    }

    fun createTodo(userId: Long, requestDto: CreateTodoRequest): Todo {
        val user = userService.getUserByUserId(userId)
        val entity = requestDto.toEntity(user)
        return todoRepository.save(entity)
    }
}
