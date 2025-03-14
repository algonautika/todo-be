package algo.todo.global.util

import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.response.DomainCode
import java.time.ZoneId
import java.util.*

class TimeUtil {

    companion object {
        fun convertToTimeZone(timeZone: String) =
            kotlin.runCatching {
                val zoneId = ZoneId.of(timeZone)
                TimeZone.getTimeZone(zoneId)
            }.onFailure {
                throw CustomException(ErrorType.INVALID_TIME_ZONE, DomainCode.UTIL)
            }
    }
}
