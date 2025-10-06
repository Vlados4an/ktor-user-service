package model.table

import model.enums.UserRole
import model.enums.UserStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Users : IntIdTable("users", "user_id") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val phone = varchar("phone", 20).nullable()
    val role = enumerationByName("role", 20, UserRole::class).default(UserRole.USER)
    val status = enumerationByName("status", 20, UserStatus::class).default(UserStatus.ACTIVE)
    val isDeleted = bool("is_deleted").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())
}