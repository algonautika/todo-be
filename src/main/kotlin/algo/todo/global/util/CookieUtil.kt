package algo.todo.global.util

import algo.todo.global.dto.DomainCode
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie

class CookieUtil {

    companion object {
        private const val SET_COOKIE = "Set-Cookie"
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"

        fun setAccessTokenAndRefreshTokenCookie(
            response: HttpServletResponse,
            accessToken: String,
            refreshToken: String
        ) {
            response.addHeader(SET_COOKIE, buildCookieHeader(ACCESS_TOKEN, accessToken).toString())
            response.addHeader(SET_COOKIE, buildCookieHeader(REFRESH_TOKEN, refreshToken).toString())
        }

        fun getAccessTokenFromCookie(request: HttpServletRequest): Result<String> =
            runCatching {
                request.cookies
                    ?.find { it.name == ACCESS_TOKEN }
                    ?.value
                    ?: throw CustomException(ErrorType.INVALID_TOKEN, DomainCode.COMMON)
            }


        fun getRefreshTokenFromCookie(request: HttpServletRequest): Result<String> =
            runCatching {
                request.cookies
                    ?.find { it.name == REFRESH_TOKEN }
                    ?.value
                    ?: throw CustomException(ErrorType.INVALID_TOKEN, DomainCode.COMMON)
            }

        /**
         * 쿠키의 전체 문자열을 구성:
         * name=value; Path=/; HttpOnly; SameSite=None; Secure
         */
        private fun buildCookieHeader(name: String, value: String): ResponseCookie =
            ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build()
    }

}
