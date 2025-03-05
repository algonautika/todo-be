package algo.todo.global.tuil

import algo.todo.global.util.CookieUtil
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

class CookieUtilUnitTest : DescribeSpec({

    describe("CookieUtil.setAccessTokenAndRefreshTokenCookie") {
        context("accessToken, refreshToken 값이 주어지면") {
            it("response 에 access_token 쿠키와 refresh_token 쿠키를 추가한다") {
                // given
                val response = mockk<HttpServletResponse>()
                val capturedCookies = mutableListOf<Cookie>() // 여러 호출의 Cookie 를 모두 담을 리스트

                // addCookie()가 호출될 때마다, 쿠키를 capturedCookies 에 추가
                every { response.addCookie(capture(capturedCookies)) } returns Unit

                val accessToken = "accTokValue"
                val refreshToken = "refTokValue"

                // when
                CookieUtil.setAccessTokenAndRefreshTokenCookie(
                    response,
                    accessToken,
                    refreshToken
                )

                // then
                // addCookie 가 정확히 2번 호출
                verify(exactly = 2) {
                    response.addCookie(any())
                }

                capturedCookies.size shouldBe 2

                val first = capturedCookies[0]
                val second = capturedCookies[1]

                first.name shouldBe "access_token"
                first.value shouldBe accessToken
                second.name shouldBe "refresh_token"
                second.value shouldBe refreshToken

                first.isHttpOnly shouldBe true
                first.path shouldBe "/"
                second.isHttpOnly shouldBe true
                second.path shouldBe "/"
            }
        }
    }
})
