package algo.todo.domain.todo.entity

import algo.todo.domain.user.entity.Users
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.envers.Audited
import java.time.LocalDateTime
import java.util.*

@Audited
@Entity
@Table(name = "todo")
class Todo private constructor(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @field:JoinColumn(name = "user_id", nullable = false)
    @field:ManyToOne(fetch = FetchType.LAZY)
    val user: Users,
    @field:Column(name = "title", columnDefinition = "varchar(100) NOT NULL", nullable = false)
    var title: String,
    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String,
    @field:Column(name = "start_date", columnDefinition = "TIMESTAMP")
    var startDate: LocalDateTime,
    @field:Column(name = "end_date", columnDefinition = "TIMESTAMP")
    var endDate: LocalDateTime,
    @field:Column(name = "deadline", columnDefinition = "TIMESTAMP")
    var deadline: LocalDateTime,
    @field:CreationTimestamp
    @field:Column(name = "created_at", columnDefinition = "TIMESTAMP NOT NULL", nullable = false)
    val createdAt: LocalDateTime,
    @field:Column(name = "time_zone", columnDefinition = "varchar(50) NOT NULL", nullable = false)
    var timeZone: TimeZone,
) {
    constructor(
        title: String,
        description: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        deadline: LocalDateTime,
        timeZone: TimeZone
    ) : this(
        id = 0,
        title = title,
        description = description,
        startDate = startDate,
        endDate = endDate,
        deadline = deadline,
        createdAt = LocalDateTime.now(),
        timeZone = timeZone,
    )
}
