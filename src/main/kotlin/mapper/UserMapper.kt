package mapper

import dto.user.UserDto
import dto.user.UserPenaltyDto
import model.entity.UserEntity
import model.entity.UserPenaltyEntity

fun UserEntity.toDto() = UserDto(
    id = id.value,
    email = email,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    role = role,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UserPenaltyEntity.toDto() = UserPenaltyDto(
    id = id.value,
    userId = userId.value,
    amount = amount,
    reason = reason,
    isPaid = isPaid,
    createdAt = createdAt,
    paidAt = paidAt
)