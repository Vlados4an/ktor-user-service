package dto.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import model.enums.UserRole
import model.enums.UserStatus
import java.time.Instant

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val role: UserRole,
    val status: UserStatus,
    @Contextual val createdAt: Instant,
    @Contextual val updatedAt: Instant
)