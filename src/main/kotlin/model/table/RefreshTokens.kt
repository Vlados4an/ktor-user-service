package model.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object RefreshTokens : IntIdTable("refresh_tokens", "token_id") {
    val userId = reference("user_id", Users)
    val token = varchar("token", 500).uniqueIndex()
    val expiresAt = timestamp("expires_at").default(Instant.now())
    val createdAt = timestamp("created_at").default(Instant.now())
    val isRevoked = bool("is_revoked").default(false)
}