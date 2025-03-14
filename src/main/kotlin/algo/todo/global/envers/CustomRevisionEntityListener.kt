package algo.todo.global.envers

import algo.todo.global.util.SecurityUtil
import org.hibernate.envers.RevisionListener

class CustomRevisionEntityListener : RevisionListener {
    override fun newRevision(revisionEntity: Any) {
        val authentication = SecurityUtil.getCustomAuthentication().getOrElse {
            return
        }

        val customRevisionEntity = revisionEntity as CustomRevisionEntity
        customRevisionEntity.byUserId = authentication.users.id
    }
}
