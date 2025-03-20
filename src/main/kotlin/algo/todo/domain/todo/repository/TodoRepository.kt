package algo.todo.domain.todo.repository

import algo.todo.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TodoRepository : JpaRepository<Todo, Long> {
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Todo>
    fun findByUserIdAndId(userId: Long, id: Long): Optional<Todo>
}
