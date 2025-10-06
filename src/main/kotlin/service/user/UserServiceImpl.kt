package service.user

import dto.auth.CreatePenaltyRequest
import dto.page.PageRequest
import dto.user.UpdateUserRequest
import dto.user.UserDto
import dto.user.UserPenaltyDto
import mapper.toDto
import model.entity.UserEntity
import model.entity.UserPenaltyEntity
import model.enums.UserStatus
import model.table.UserPenalties
import model.table.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.clevertec.exception.EntityNotFoundException
import java.math.BigDecimal

class UserServiceImpl : UserService {

    override fun getUserById(id: Int): UserDto = transaction {
        val user = UserEntity.find {
            (Users.id eq id) and (Users.isDeleted eq false)
        }.firstOrNull() ?: throw EntityNotFoundException("User with id $id not found")

        user.toDto()
    }

    override fun getCurrentUser(userId: Int): UserDto = getUserById(userId)

    override fun updateCurrentUser(userId: Int, request: UpdateUserRequest): UserDto = transaction {
        val user = UserEntity.find {
            (Users.id eq userId) and (Users.isDeleted eq false)
        }.firstOrNull() ?: throw EntityNotFoundException("User with id $id not found")

        request.firstName?.let { user.firstName = it }
        request.lastName?.let { user.lastName = it }
        request.phone?.let { user.phone = it }

        user.toDto()
    }

    override fun getAllUsers(pageRequest: PageRequest): List<UserDto> = transaction {
        UserEntity.find { Users.isDeleted eq false }
            .limit(pageRequest.size).offset((pageRequest.offset))
            .map { it.toDto() }
    }

    override fun blockUser(userId: Int): UserDto = transaction {
        val user = UserEntity.find {
            (Users.id eq userId) and (Users.isDeleted eq false)
        }.firstOrNull() ?: throw EntityNotFoundException("User with id $userId not found")

        user.status = UserStatus.BLOCKED
        user.toDto()
    }

    override fun unblockUser(userId: Int): UserDto = transaction {
        val user = UserEntity.find {
            (Users.id eq userId) and (Users.isDeleted eq false)
        }.firstOrNull() ?: throw EntityNotFoundException("User with id $userId not found")

        user.status = UserStatus.ACTIVE
        user.toDto()
    }

    override fun getUserPenalties(userId: Int): List<UserPenaltyDto> = transaction {
        UserPenaltyEntity.find { UserPenalties.userId eq userId }
            .orderBy(UserPenalties.createdAt to org.jetbrains.exposed.sql.SortOrder.DESC)
            .map { it.toDto() }
    }

    override fun createPenalty(request: CreatePenaltyRequest): UserPenaltyDto = transaction {
        UserEntity.find {
            (Users.id eq request.userId) and (Users.isDeleted eq false)
        }.firstOrNull() ?: throw EntityNotFoundException("User with id ${request.userId} not found")

        val penalty = UserPenaltyEntity.new {
            this.userId = EntityID(request.userId, Users)
            this.amount = BigDecimal(request.amount)
            this.reason = request.reason
            this.isPaid = false
        }

        penalty.toDto()
    }
}