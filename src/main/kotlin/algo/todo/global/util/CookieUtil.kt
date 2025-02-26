package algo.todo.global.util

import jakarta.servlet.http.Cookie
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
            val accessTokenCookie = Cookie(ACCESS_TOKEN, accessToken)
            val refreshTokenCookie = Cookie(REFRESH_TOKEN, refreshToken)

            accessTokenCookie.isHttpOnly = true
            accessTokenCookie.path = "/"
            refreshTokenCookie.isHttpOnly = true
            refreshTokenCookie.path = "/"

            response.addCookie(accessTokenCookie)
            response.addCookie(refreshTokenCookie)
        }
    }


}
