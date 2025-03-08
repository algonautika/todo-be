package algo.todo.global.util

import algo.todo.global.dto.DomainCode
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.security.CustomUserDetails
import org.springframework.security.core.context.SecurityContextHolder

class SecurityUtil {

    companion object {
        fun getCustomAuthentication(): Result<CustomUserDetails> =
            runCatching {
                SecurityContextHolder.getContext().authentication
                    ?.principal as? CustomUserDetails
                    ?: throw CustomException(ErrorType.UNAUTHORIZED, DomainCode.USER)
            }
    }
}
