package model.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object UserPenalties : IntIdTable("user_penalties", "penalty_id") {
    val userId = reference("user_id", Users)
    val amount = decimal("amount", 10, 2)
    val reason = varchar("reason", 255)
    val isPaid = bool("is_paid").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())
    val paidAt = timestamp("paid_at").nullable()
}