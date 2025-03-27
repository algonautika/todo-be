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
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime
import java.util.*

class TodoUnitService : BehaviorSpec({

    val todoRepository = mockk<TodoRepository>()
    val userService = mockk<UserService>()
    val todoService = TodoService(userService, todoRepository)

    Given("findByUserIdAndId 조회 시 해당 Todo가 존재하지 않는 상황에서") {
        // "전제 상태"를 설정
        every { todoRepository.findByUserIdAndId(3L, 999L) } returns Optional.empty()

        When("getTodoDetail 메서드를 호출하면") {
            // "행동"을 수행
            val thrown = shouldThrow<CustomException> {
                todoService.getTodoDetail(3L, 999L)
            }

            Then("NOT_FOUND_TODO 예외(CustomException)가 발생해야 한다") {
                // "결과" 검증
                thrown.errorType shouldBe ErrorType.NOT_FOUND_TODO
                thrown.domainCode shouldBe DomainCode.TODO

                verify(exactly = 1) {
                    todoRepository.findByUserIdAndId(3L, 999L)
                }
            }
        }
    }

    Given("올바른 CreateTodoRequest가 주어졌을 때") {
        // 전제(Given) - 테스트 준비
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

        val todoEntity = requestDto.toEntity(user).also {
            ReflectionTestUtils.setField(it, "id", 10)
        }

        // 목(Mock) 동작 정의
        every { userService.getUserByUserId(userId) } returns user
        every { todoRepository.save(any()) } returns todoEntity

        When("createTodo 메서드를 호출하면") {
            // 행동(When) - 실제 메서드 호출
            val result = todoService.createTodo(userId, requestDto)

            Then("새로운 Todo가 생성되어 저장된다") {
                // 결과(Then) - 검증
                result.id shouldBe 10
                result.title shouldBe "Test Title"
                result.user shouldBe user

                verify(exactly = 1) { userService.getUserByUserId(userId) }
                verify(exactly = 1) { todoRepository.save(any()) }
            }
        }
    }

    Given("존재하는 userId=1, todoId=100, 그리고 유효한 UpdateTodoRequest가 주어진 상황") {
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
        ).also {
            // DB에 저장된 상태라고 가정
            ReflectionTestUtils.setField(it, "id", todoId)
        }

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

        // getTodoDetail 내부에서 호출되는 레포지토리 동작 Mock
        every { todoRepository.findByUserIdAndId(userId, todoId) } returns Optional.of(existingTodo)

        When("updateTodo(userId, todoId, updateRequest)를 호출하면") {
            todoService.updateTodo(userId, todoId, updateRequest)

            Then("해당 Todo의 필드가 업데이트 요청 내용으로 변경된다") {
                existingTodo.title shouldBe "New Title"
                existingTodo.description shouldBe "New Description"
                existingTodo.startDate shouldBe updateRequest.startDate
                existingTodo.endDate shouldBe updateRequest.endDate
                existingTodo.deadline shouldBe updateRequest.deadline
                existingTodo.timeZone shouldBe TimeUtil.convertToTimeZone(updateRequest.timeZone).getOrNull()

                verify(exactly = 1) {
                    todoRepository.findByUserIdAndId(userId, todoId)
                }
            }
        }
    }
})
