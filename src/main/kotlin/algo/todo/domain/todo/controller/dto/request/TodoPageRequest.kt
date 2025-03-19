package algo.todo.domain.todo.controller.dto.request

import algo.todo.global.request.BasePageRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class TodoPageRequest(
    val base: BasePageRequest = BasePageRequest(),
    val sort: String = "createdAt:desc",
    val preview: String = "description:500",
) {

    private val VALID_SORT_FIELDS = setOf(
        "createdAt", "startDate", "endDate", "deadline"
    )

    fun parsePreview(): Pair<String, Int> {
        val parts = preview.split(":")
        val defaultPreview = "description" to 500

        return when {
            parts.size == 2 -> parts[0] to parts[1].toInt()
            // 파싱 오류 시 기본값
            else -> defaultPreview
        }
    }

    /**
     * 페이지네이션 + 정렬 정보가 담긴 PageRequest 를 만들어 반환
     */
    fun toPageRequest(): PageRequest {
        val (sortField, sortDirection) = parseSort(sort)

        val direction = if (sortDirection.equals(Sort.Direction.DESC.name, ignoreCase = true)) {
            Sort.Direction.DESC
        } else {
            Sort.Direction.ASC
        }

        return PageRequest.of(
            base.page,
            base.pageSize,
            Sort.by(direction, sortField)
        )
    }

    private fun parseSort(sort: String): Pair<String, String> {
        val parts = sort.split(":")
        val defaultSort = "createdAt" to "desc"

        return when {
            parts.size == 2 -> {
                val field = parts[0]
                val direction = parts[1]

                // 정렬 필드가 유효한지 확인
                if (field in VALID_SORT_FIELDS) {
                    return field to direction
                }

                // 유효하지 않은 필드일 경우 기본값
                return defaultSort
            }
            // 파싱 오류 시 기본값
            else -> defaultSort
        }
    }
}


