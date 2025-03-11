package algo.todo.global.tuil

import algo.todo.global.exception.ErrorType
import algo.todo.global.response.ApiErrorResponse
import algo.todo.global.response.DomainCode
import algo.todo.global.util.ExceptionUtil
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

class ExceptionUtilTest : DescribeSpec({

    describe("writeErrorJson 함수") {

        context("에러 정보를 담아 response 에 JSON 을 쓰는 경우") {
            it("HTTP 응답의 contentType, status, writer 에 올바른 값이 설정되어야 한다") {
                // given
                val response = mockk<HttpServletResponse>(relaxed = true)

                val printWriter = mockk<PrintWriter>(relaxed = true)
                every { response.writer } returns printWriter

                val errorType = ErrorType.INVALID_TOKEN
                val domainCode = DomainCode.COMMON

                // when
                ExceptionUtil.writeErrorJson(response, errorType, domainCode)

                // then
                verify { response.contentType = MediaType.APPLICATION_JSON_VALUE }
                verify { response.characterEncoding = StandardCharsets.UTF_8.name() }
                verify { response.status = errorType.status.value() }

                val slotString = slot<String>()
                verify {
                    printWriter.write(capture(slotString))
                    printWriter.flush()
                    printWriter.close()
                }

                val writtenJson = slotString.captured
                val mapper = ObjectMapper()

                val dto =
                    ApiErrorResponse(
                        errorType = errorType,
                        domainCode = domainCode,
                    )

                val parsed = mapper.readValue(writtenJson, Map::class.java)
                parsed["status"] shouldBe dto.status
                parsed["message"] shouldBe dto.message
            }
        }
    }
})
