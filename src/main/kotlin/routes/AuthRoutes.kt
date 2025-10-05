package ru.clevertec.routes

import dto.auth.LoginRequest
import dto.auth.RefreshTokenRequest
import dto.auth.RegisterRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import service.auth.AuthService

fun Route.authRoutes(authService: AuthService) {
    route("/api/v1/auth") {

        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val user = authService.register(request)
                call.respond(HttpStatusCode.Created, user)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val response = authService.login(request)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to e.message))
            }
        }

        post("/refresh") {
            try {
                val request = call.receive<RefreshTokenRequest>()
                val response = authService.refreshAccessToken(request.refreshToken)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to e.message))
            }
        }

        post("/logout") {
            try {
                val request = call.receive<RefreshTokenRequest>()
                authService.logout(request.refreshToken)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Logged out successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}