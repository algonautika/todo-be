package algo.todo.global.envers

import jakarta.persistence.*
import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionNumber
import org.hibernate.envers.RevisionTimestamp

@Entity
@RevisionEntity(CustomRevisionEntityListener::class)
@Table(name = "revision_history")
class CustomRevisionEntity {

    @Id
    @Column
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val revisionId: Long = 0

    @Column
    @RevisionTimestamp
    val updatedAt: Long = 0

    @Column
    var byUserId: Long? = null
}
