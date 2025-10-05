package service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dto.auth.AuthResponse
import dto.auth.LoginRequest
import dto.auth.RegisterRequest
import dto.user.UserDto
import mapper.toDto
import model.entity.UserEntity
import model.enums.UserRole
import model.enums.UserStatus
import model.table.RefreshTokens
import model.table.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

class AuthService(
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val accessTokenExpiration: Long = 3600, // 1 час
    private val refreshTokenExpiration: Long = 604800 // 7 дней
) {
    
    fun register(request: RegisterRequest): UserDto = transaction {
        // Проверка существования пользователя
        val existingUser = UserEntity.find { Users.email eq request.email }.firstOrNull()
        if (existingUser != null) {
            throw IllegalArgumentException("User with this email already exists")
        }

        // Хэширование пароля
        val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())

        // Создание пользователя
        val now = Instant.now()
        val user = UserEntity.new {
            email = request.email
            this.passwordHash = passwordHash
            firstName = request.firstName
            lastName = request.lastName
            phone = request.phone
            role = UserRole.USER
            status = UserStatus.ACTIVE
            isDeleted = false
            createdAt = now
            updatedAt = now
        }

        user.toDto()
    }

    fun login(request: LoginRequest): AuthResponse = transaction {
        // Поиск пользователя
        val user = UserEntity.find { 
            (Users.email eq request.email) and (Users.isDeleted eq false) 
        }.firstOrNull() ?: throw IllegalArgumentException("Invalid credentials")

        // Проверка статуса
        if (user.status != UserStatus.ACTIVE) {
            throw IllegalArgumentException("User account is ${user.status.name.lowercase()}")
        }

        // Проверка пароля
        if (!BCrypt.checkpw(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        // Генерация токенов
        generateTokens(user.id.value, user.email, user.role)
    }

    fun refreshAccessToken(refreshToken: String): AuthResponse = transaction {
        // Проверка refresh token
        val tokenRow = RefreshTokens.select(
            (RefreshTokens.token eq refreshToken) and
                    (RefreshTokens.isRevoked eq false)
        ).firstOrNull() ?: throw IllegalArgumentException("Invalid refresh token")

        val expiresAt = tokenRow[RefreshTokens.expiresAt]
        if (expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("Refresh token expired")
        }

        val userId = tokenRow[RefreshTokens.userId].value
        val user = UserEntity[userId]

        // Генерация новых токенов
        generateTokens(user.id.value, user.email, user.role)
    }

    fun logout(refreshToken: String) = transaction {
        RefreshTokens.update({ RefreshTokens.token eq refreshToken }) {
            it[isRevoked] = true
        }
    }

    private fun generateTokens(userId: Int, email: String, role: UserRole): AuthResponse {
        val now = Instant.now()
        
        // Access Token
        val accessToken = JWT.create()
            .withIssuer(jwtIssuer)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("role", role.name)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(accessTokenExpiration)))
            .sign(Algorithm.HMAC256(jwtSecret))

        // Refresh Token
        val refreshToken = UUID.randomUUID().toString()
        RefreshTokens.insert {
            it[RefreshTokens.userId] = userId
            it[token] = refreshToken
            it[expiresAt] = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS)
            it[createdAt] = now
            it[isRevoked] = false
        }

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = accessTokenExpiration
        )
    }
}