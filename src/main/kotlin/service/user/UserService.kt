package service.user

import dto.auth.CreatePenaltyRequest
import dto.page.PageRequest
import dto.user.UpdateUserRequest
import dto.user.UserDto
import dto.user.UserPenaltyDto

interface UserService {
    fun getUserById(id: Int): UserDto
    fun getCurrentUser(userId: Int): UserDto
    fun updateCurrentUser(userId: Int, request: UpdateUserRequest): UserDto
    fun getAllUsers(pageRequest: PageRequest): List<UserDto>
    fun blockUser(userId: Int): UserDto
    fun unblockUser(userId: Int): UserDto
    fun getUserPenalties(userId: Int): List<UserPenaltyDto>
    fun createPenalty(request: CreatePenaltyRequest): UserPenaltyDto
}