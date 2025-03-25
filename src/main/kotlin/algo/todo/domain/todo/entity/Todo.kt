package algo.todo.domain.todo.entity

import algo.todo.domain.user.entity.Users
import algo.todo.global.util.TimeUtil
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
        user: Users,
        title: String,
        description: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        deadline: LocalDateTime,
        timeZone: String
    ) : this(
        id = 0,
        user = user,
        title = title,
        description = description,
        startDate = startDate,
        endDate = endDate,
        deadline = deadline,
        createdAt = LocalDateTime.now(),
        timeZone = TimeUtil.convertToTimeZone(timeZone).getOrThrow()
    )

    fun update(
        title: String,
        description: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        deadline: LocalDateTime,
        timeZone: String
    ) {
        this.title = title
        this.description = description
        this.startDate = startDate
        this.endDate = endDate
        this.deadline = deadline
        this.timeZone = TimeUtil.convertToTimeZone(timeZone).getOrThrow()
    }
}
