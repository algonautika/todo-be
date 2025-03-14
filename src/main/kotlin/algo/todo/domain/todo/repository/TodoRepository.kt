package algo.todo.domain.todo.repository

import algo.todo.domain.todo.entity.Todo
import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<Todo, Long> {
//    fun findAllByUserId(userId: Long): List<Todo>
}
