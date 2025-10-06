package ru.clevertec.routes

import dto.auth.LoginRequest
import dto.auth.RefreshTokenRequest
import dto.auth.RegisterRequest
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import ru.clevertec.validator.validatedReceive
import service.auth.AuthService

fun Route.authRoutes() {
    val authService by closestDI().instance<AuthService>()

    route("/api/v1/auth") {

        post("/register") {
            val request = call.validatedReceive<RegisterRequest>()
            val user = authService.register(request)
            call.respond(HttpStatusCode.Created, user)
        }

        post("/login") {
            val request = call.validatedReceive<LoginRequest>()
            val response = authService.login(request)
            call.respond(HttpStatusCode.OK, response)
        }

        post("/refresh") {
            val request = call.validatedReceive<RefreshTokenRequest>()
            val response = authService.refreshAccessToken(request.refreshToken)
            call.respond(HttpStatusCode.OK, response)
        }

        post("/logout") {
            val request = call.validatedReceive<RefreshTokenRequest>()
            authService.logout(request.refreshToken)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Logged out successfully"))
        }
    }
}