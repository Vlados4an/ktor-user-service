package service.user

import dto.user.UpdateUserRequest
import dto.user.UserDto
import dto.user.UserPenaltyDto
import mapper.toDto
import model.entity.UserEntity
import model.entity.UserPenaltyEntity
import model.enums.UserStatus
import model.table.UserPenalties
import model.table.Users
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class UserService {

    fun getUserById(id: Int): UserDto = transaction {
        val user = UserEntity.find { 
            (Users.id eq id) and (Users.isDeleted eq false) 
        }.firstOrNull() ?: throw NoSuchElementException("User not found")
        
        user.toDto()
    }

    fun getCurrentUser(userId: Int): UserDto = getUserById(userId)

    fun updateCurrentUser(userId: Int, request: UpdateUserRequest): UserDto = transaction {
        val user = UserEntity.find { 
            (Users.id eq userId) and (Users.isDeleted eq false) 
        }.firstOrNull() ?: throw NoSuchElementException("User not found")

        request.firstName?.let { user.firstName = it }
        request.lastName?.let { user.lastName = it }
        request.phone?.let { user.phone = it }
        user.updatedAt = Instant.now()

        user.toDto()
    }

    fun getAllUsers(page: Int = 1, size: Int = 20): List<UserDto> = transaction {
        UserEntity.find { Users.isDeleted eq false }
            .limit(size).offset((page - 1) * size.toLong())
            .map { it.toDto() }
    }

    fun blockUser(userId: Int): UserDto = transaction {
        val user = UserEntity[userId]
        user.status = UserStatus.BLOCKED
        user.updatedAt = Instant.now()
        user.toDto()
    }

    fun unblockUser(userId: Int): UserDto = transaction {
        val user = UserEntity[userId]
        user.status = UserStatus.ACTIVE
        user.updatedAt = Instant.now()
        user.toDto()
    }

    fun getUserPenalties(userId: Int): List<UserPenaltyDto> = transaction {
        UserPenaltyEntity.find { UserPenalties.userId eq userId }
            .orderBy(UserPenalties.createdAt to org.jetbrains.exposed.sql.SortOrder.DESC)
            .map { it.toDto() }
    }
}