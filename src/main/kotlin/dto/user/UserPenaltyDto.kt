package dto.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class UserPenaltyDto(
    val id: Int,
    val userId: Int,
    @Contextual val amount: BigDecimal,
    val reason: String,
    val isPaid: Boolean,
    @Contextual val createdAt: Instant,
    @Contextual val paidAt: Instant?
)
