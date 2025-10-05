package dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null
)