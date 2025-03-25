package algo.todo.domain.todo.service

import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.controller.dto.request.TodoPageRequest
import algo.todo.domain.todo.controller.dto.request.UpdateTodoRequest
import algo.todo.domain.todo.entity.Todo
import algo.todo.domain.todo.repository.TodoRepository
import algo.todo.domain.user.service.UserService
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoService(
    private val userService: UserService,
    private val todoRepository: TodoRepository
) {
    @Transactional(readOnly = true)
    fun getTodosWithPagination(userId: Long, todoPageRequest: TodoPageRequest): Page<Todo> {
        val pageRequest = todoPageRequest.toPageRequest()
        return todoRepository.findAllByUserId(
            userId = userId,
            pageable = pageRequest
        )
    }

    @Transactional(readOnly = true)
    fun getTodoDetail(userId: Long, todoId: Long): Todo =
        todoRepository.findByUserIdAndId(
            userId = userId,
            id = todoId
        ).orElseThrow { throw CustomException(ErrorType.NOT_FOUND_TODO, DomainCode.TODO) }

    @Transactional
    fun createTodo(userId: Long, requestDto: CreateTodoRequest): Todo {
        val user = userService.getUserByUserId(userId)
        val entity = requestDto.toEntity(user)
        return todoRepository.save(entity)
    }

    @Transactional
    fun updateTodo(userId: Long, todoId: Long, requestDto: UpdateTodoRequest) {
        val todo = getTodoDetail(userId, todoId)
        todo.update(
            title = requestDto.title,
            description = requestDto.description,
            startDate = requestDto.startDate,
            endDate = requestDto.endDate,
            deadline = requestDto.deadline,
            timeZone = requestDto.timeZone
        )
    }
}
