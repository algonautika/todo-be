package algo.todo.domain.todo.repository

import algo.todo.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<Todo, Long> {
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Todo>
}
