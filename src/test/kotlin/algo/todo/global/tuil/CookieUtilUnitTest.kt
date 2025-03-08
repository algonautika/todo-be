package algo.todo.global.tuil

import algo.todo.global.util.CookieUtil
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletResponse

class CookieUtilUnitTest : DescribeSpec({

    describe("CookieUtil.setAccessTokenAndRefreshTokenCookie") {
        context("accessToken, refreshToken 값이 주어지면") {
            it("response 에 access_token 쿠키와 refresh_token 쿠키를 추가한다") {
                // given
                val response = mockk<HttpServletResponse>(relaxed = true)
                // 캡처할 리스트: Pair<헤더명, 헤더값>
                val capturedHeaders = mutableListOf<Pair<String, String>>()

                every { response.addHeader(any(), any()) } answers { call ->
                    val headerName = call.invocation.args[0] as String
                    val headerValue = call.invocation.args[1] as String
                    capturedHeaders.add(headerName to headerValue)
                    Unit
                }

                val accessToken = "accTokValue"
                val refreshToken = "refTokValue"

                // when
                CookieUtil.setAccessTokenAndRefreshTokenCookie(response, accessToken, refreshToken)

                // then
                // addHeader 가 정확히 2번 호출되었는지 확인
                verify(exactly = 2) { response.addHeader(any(), any()) }
                capturedHeaders.size shouldBe 2

                // 각 헤더 문자열이 올바른 쿠키 형식을 갖추었는지 검증
                val accessHeader = capturedHeaders.find { it.second.contains("access_token") }
                val refreshHeader = capturedHeaders.find { it.second.contains("refresh_token") }

                accessHeader shouldNotBe null
                refreshHeader shouldNotBe null

                // accessHeader 검증
                with(accessHeader!!.second) {
                    this shouldBe (this) // 단순 검증: 문자열이 비어있지 않은지
                    contains("access_token=accTokValue") shouldBe true
                    contains("Path=/") shouldBe true
                    contains("HttpOnly") shouldBe true
                    contains("SameSite=None") shouldBe true
                    contains("Secure") shouldBe true
                }

                // refreshHeader 검증
                with(refreshHeader!!.second) {
                    contains("refresh_token=refTokValue") shouldBe true
                    contains("Path=/") shouldBe true
                    contains("HttpOnly") shouldBe true
                    contains("SameSite=None") shouldBe true
                    contains("Secure") shouldBe true
                }
            }
        }
    }
})
