package service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dto.auth.AuthResponse
import dto.auth.LoginRequest
import dto.auth.RegisterRequest
import dto.user.UserDto
import mapper.toDto
import model.entity.RefreshTokenEntity
import model.entity.UserEntity
import model.enums.UserRole
import model.enums.UserStatus
import model.table.RefreshTokens
import model.table.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import ru.clevertec.exception.InvalidCredentialsException
import ru.clevertec.exception.InvalidTokenException
import ru.clevertec.exception.UserAlreadyExistsException
import ru.clevertec.exception.UserBlockedException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class AuthServiceImpl(
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val accessTokenExpiration: Long = 3600,
    private val refreshTokenExpiration: Long = 604800
) : AuthService {

    override fun register(request: RegisterRequest): UserDto = transaction {
        val existingUser = UserEntity.find { Users.email eq request.email }.firstOrNull()
        if (existingUser != null) {
            throw UserAlreadyExistsException("User with this email already exists")
        }

        val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())

        val user = UserEntity.new {
            email = request.email
            this.passwordHash = passwordHash
            firstName = request.firstName
            lastName = request.lastName
            phone = request.phone
            role = UserRole.USER
            status = UserStatus.ACTIVE
            isDeleted = false
        }

        user.toDto()
    }

    override fun login(request: LoginRequest): AuthResponse = transaction {
        val user = UserEntity.find {
            (Users.email eq request.email) and (Users.isDeleted eq false)
        }.firstOrNull() ?: throw InvalidCredentialsException("Invalid credentials")

        if (user.status != UserStatus.ACTIVE) {
            throw UserBlockedException("User account is ${user.status.name.lowercase()}")
        }

        if (!BCrypt.checkpw(request.password, user.passwordHash)) {
            throw InvalidCredentialsException("Invalid credentials")
        }

        generateTokens(user.id.value, user.email, user.role)
    }

    override fun refreshAccessToken(refreshToken: String): AuthResponse = transaction {
        val tokenEntity = RefreshTokenEntity.find {
            (RefreshTokens.token eq refreshToken) and
                    (RefreshTokens.isRevoked eq false)
        }.firstOrNull() ?: throw InvalidTokenException("Invalid refresh token")

        if (tokenEntity.expiresAt.isBefore(Instant.now())) {
            throw InvalidTokenException("Refresh token expired")
        }

        val user = UserEntity[tokenEntity.userId.value]

        generateTokens(user.id.value, user.email, user.role)
    }

    override fun logout(refreshToken: String) {
        transaction {
            RefreshTokenEntity.find { RefreshTokens.token eq refreshToken }
                .firstOrNull()
                ?.apply { isRevoked = true }
        }
    }

    private fun generateTokens(userId: Int, email: String, role: UserRole): AuthResponse {
        val now = Instant.now()

        val accessToken = JWT.create()
            .withIssuer(jwtIssuer)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("role", role.name)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(accessTokenExpiration)))
            .sign(Algorithm.HMAC256(jwtSecret))

        val refreshToken = UUID.randomUUID().toString()
        RefreshTokenEntity.new {
            this.userId = EntityID(userId, Users)
            this.token = refreshToken
            this.expiresAt = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS)
            this.isRevoked = false
        }

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = accessTokenExpiration
        )
    }
}