package algo.todo.domain.todo.service

import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.controller.dto.request.UpdateTodoRequest
import algo.todo.domain.todo.entity.Todo
import algo.todo.domain.todo.repository.TodoRepository
import algo.todo.domain.user.entity.Users
import algo.todo.domain.user.service.UserService
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode
import algo.todo.global.security.ProviderType
import algo.todo.global.util.TimeUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime
import java.util.*

class TodoUnitService : DescribeSpec({

    val todoRepository = mockk<TodoRepository>()
    val userService = mockk<UserService>()
    val todoService = TodoService(userService, todoRepository)

    describe("TodoService.getTodoDetail()") {
        context("특정 userId와 todoId가 주어졌을 때") {
            it("존재하지 않는 경우 예외(CustomException)를 발생시킨다") {
                // given
                val userId = 3L
                val todoId = 999L

                every { todoRepository.findByUserIdAndId(userId, todoId) } returns Optional.empty()

                // when & then
                val thrown = shouldThrow<CustomException> {
                    todoService.getTodoDetail(userId, todoId)
                }
                // 예외 메시지나 ErrorType 등을 필요에 맞게 검증
                thrown.errorType shouldBe ErrorType.NOT_FOUND_TODO
                thrown.domainCode shouldBe DomainCode.TODO

                verify(exactly = 1) { todoRepository.findByUserIdAndId(userId, todoId) }
            }
        }
    }

    describe("TodoService.createTodo()") {
        context("올바른 CreateTodoRequest 가 주어졌을 때") {
            it("새로운 Todo 를 생성하고 저장한다") {
                // given
                val userId = 1L
                val user = Users(
                    email = "test@example.com",
                    providerType = ProviderType.GOOGLE,
                    providerId = "google-1234"
                )

                val requestDto = CreateTodoRequest(
                    title = "Test Title",
                    description = "description",
                    startDate = LocalDateTime.now(),
                    endDate = LocalDateTime.now(),
                    deadline = LocalDateTime.now(),
                    timeZone = "GMT+9"
                )

                val todoEntity = requestDto.toEntity(user)
                ReflectionTestUtils.setField(todoEntity, "id", 10)

                // Mock 동작 정의
                every { userService.getUserByUserId(userId) } returns user
                every { todoRepository.save(any()) } returns todoEntity

                // when
                val result = todoService.createTodo(userId, requestDto)

                // then
                result.id shouldBe 10
                result.title shouldBe "Test Title"
                result.user shouldBe user

                verify(exactly = 1) { userService.getUserByUserId(userId) }
                verify(exactly = 1) { todoRepository.save(any()) }
            }
        }
    }

    describe("TodoService.updateTodo()") {
        context("존재하는 userId, todoId가 주어지고 UpdateTodoRequest가 유효할 때") {
            it("해당 Todo의 필드를 업데이트한다") {
                // given
                val userId = 1L
                val todoId = 100L
                val beforeDate = LocalDateTime.of(2025, 3, 26, 0, 0)
                val user = Users(
                    email = "test@example.com",
                    providerType = ProviderType.GOOGLE,
                    providerId = "google-1234"
                )
                val existingTodo = Todo(
                    user = user,
                    title = "Old Title",
                    description = "Old Desc",
                    startDate = beforeDate,
                    endDate = beforeDate,
                    deadline = beforeDate,
                    timeZone = "GMT+9"
                )

                // DB에 저장된 상태라고 가정
                ReflectionTestUtils.setField(existingTodo, "id", todoId)

                // 업데이트 요청
                val afterDate = LocalDateTime.of(2025, 12, 31, 0, 0)
                val updateRequest = UpdateTodoRequest(
                    title = "New Title",
                    description = "New Description",
                    startDate = beforeDate,
                    endDate = afterDate,
                    deadline = afterDate,
                    timeZone = "GMT+9"
                )

                // getTodoDetail 내부 메서드 모킹
                every { todoRepository.findByUserIdAndId(userId, todoId) } returns Optional.of(existingTodo)

                // when
                todoService.updateTodo(userId, todoId, updateRequest)

                // then
                existingTodo.title shouldBe "New Title"
                existingTodo.description shouldBe "New Description"
                existingTodo.startDate shouldBe updateRequest.startDate
                existingTodo.endDate shouldBe updateRequest.endDate
                existingTodo.deadline shouldBe updateRequest.deadline
                existingTodo.timeZone shouldBe TimeUtil.convertToTimeZone(updateRequest.timeZone).getOrNull()

                // verify
                verify(exactly = 1) { todoRepository.findByUserIdAndId(userId, todoId) }
            }
        }
    }
})
