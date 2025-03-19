package algo.todo.domain.todo.controller.dto.request

import algo.todo.global.request.BasePageRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class TodoPageRequest(
    val base: BasePageRequest = BasePageRequest(),
    val sort: String = "createdAt:desc",
    val previewSize: Int = 500
) {
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

        return when {
            parts.size == 2 -> parts[0] to parts[1]
            else -> "createdAt" to "desc" // 파싱 오류 시 기본값
        }
    }
}


