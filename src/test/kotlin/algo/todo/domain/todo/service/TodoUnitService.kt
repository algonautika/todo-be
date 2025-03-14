package algo.todo.domain.todo.service

import algo.todo.domain.todo.controller.dto.request.CreateTodoRequest
import algo.todo.domain.todo.repository.TodoRepository
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class TodoUnitService : DescribeSpec({

    describe("TodoService.createTodo()") {
        context("올바른 CreateTodoRequest 가 주어졌을 때") {
            it("요청을 Entity 로 변환 후 Repository 에 저장하고 저장된 Todo 를 반환한다") {
                // given
                val mockRepository = mockk<TodoRepository>()
                val todoService = TodoService(mockRepository)

                val requestDto = CreateTodoRequest(
                    title = "title",
                    description = "description",
                    startDate = LocalDateTime.now(),
                    endDate = LocalDateTime.now(),
                    deadline = LocalDateTime.now(),
                    timeZone = "GMT+9"
                )

                val mockEntity = requestDto.toEntity()

                every { mockRepository.save(any()) } returns mockEntity

                // when
                val result = todoService.createTodo(requestDto)

                // then
                result shouldBe mockEntity
                verify(exactly = 1) { mockRepository.save(any()) }
            }
        }

        // todo: Util Test 로 이동
        context("유효하지 않은 TimeZone 이 주어졌을 때") {
            it("util 에러가 발생한다.") {
                // given
                val mockRepository = mockk<TodoRepository>()
                val todoService = TodoService(mockRepository)

                val requestDto = CreateTodoRequest(
                    title = "title",
                    description = "description",
                    startDate = LocalDateTime.now(),
                    endDate = LocalDateTime.now(),
                    deadline = LocalDateTime.now(),
                    timeZone = "Invalid_Timezone"
                )

                // when
                val ex = shouldThrow<CustomException> {
                    todoService.createTodo(requestDto)
                }

                // then
                ex.errorType shouldBe ErrorType.INVALID_TIME_ZONE
                ex.domainCode shouldBe DomainCode.UTIL
            }
        }
    }
})
