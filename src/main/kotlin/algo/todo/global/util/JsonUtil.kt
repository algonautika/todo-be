package algo.todo.global.util

import com.fasterxml.jackson.databind.ObjectMapper

class JsonUtil {
    companion object {
        private val objectMapper = ObjectMapper()

        fun toJsonString(obj: Any): String {
            return objectMapper.writeValueAsString(obj)
        }
    }
}
