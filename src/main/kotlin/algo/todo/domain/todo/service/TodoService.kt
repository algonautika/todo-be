package algo.todo.domain.todo.service

import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.entity.Todo
import algo.todo.domain.todo.repository.TodoRepository
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val todoRepository: TodoRepository
) {
    fun getTodos(userId: Long): List<Todo> {
        return todoRepository.findAll()
    }

    fun createTodo(requestDto: CreateTodoRequest): Todo {
        val entity = requestDto.toEntity()
        return todoRepository.save(entity)
    }
}
