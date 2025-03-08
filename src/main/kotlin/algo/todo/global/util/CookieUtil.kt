package algo.todo.global.util

import jakarta.servlet.http.HttpServletResponse

class CookieUtil {

    companion object {
        private const val ACCESS_TOKEN = "access_token";
        private const val REFRESH_TOKEN = "refresh_token";

        fun setAccessTokenAndRefreshTokenCookie(
            response: HttpServletResponse,
            accessToken: String,
            refreshToken: String
        ) {
            val accessCookieValue = buildCookieHeader(ACCESS_TOKEN, accessToken)
            response.addHeader("Set-Cookie", accessCookieValue)

            val refreshCookieValue = buildCookieHeader(REFRESH_TOKEN, refreshToken)
            response.addHeader("Set-Cookie", refreshCookieValue)
        }


        /**
         * 쿠키의 전체 문자열을 구성:
         * name=value; Path=/; HttpOnly; SameSite=None; Secure
         */
        private fun buildCookieHeader(name: String, value: String): String {
            return "$name=$value; Path=/; HttpOnly; SameSite=None; Secure"
        }
    }


}
