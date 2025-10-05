package model.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object UserActivityLogs : IntIdTable("user_activity_logs", "activity_id") {
    val userId = reference("user_id", Users)
    val action = varchar("action", 100)
    val details = text("details").nullable()
    val ipAddress = varchar("ip_address", 50).nullable()
    val createdAt = timestamp("created_at")
}