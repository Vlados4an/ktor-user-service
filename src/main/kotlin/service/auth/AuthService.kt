package service.auth

import dto.auth.AuthResponse
import dto.auth.LoginRequest
import dto.auth.RegisterRequest
import dto.user.UserDto

interface AuthService {
    fun register(request: RegisterRequest): UserDto
    fun login(request: LoginRequest): AuthResponse
    fun refreshAccessToken(refreshToken: String): AuthResponse
    fun logout(refreshToken: String)
}