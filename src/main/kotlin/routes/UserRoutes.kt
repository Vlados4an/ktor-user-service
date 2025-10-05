package ru.clevertec.routes

import dto.user.UpdateUserRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.enums.UserRole
import service.user.UserService

fun Route.userRoutes(userService: UserService) {

    authenticate("auth-jwt") {
        route("/api/v1/users") {

            // Текущий пользователь
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toInt()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                try {
                    val user = userService.getCurrentUser(userId)
                    call.respond(HttpStatusCode.OK, user)
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                }
            }

            put("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)

                try {
                    val request = call.receive<UpdateUserRequest>()
                    val user = userService.updateCurrentUser(userId, request)
                    call.respond(HttpStatusCode.OK, user)
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                }
            }

            // Только для ADMIN
            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != UserRole.ADMIN.name) {
                    return@get call.respond(HttpStatusCode.Forbidden)
                }

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))

                try {
                    val user = userService.getUserById(id)
                    call.respond(HttpStatusCode.OK, user)
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                }
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != UserRole.ADMIN.name) {
                    return@get call.respond(HttpStatusCode.Forbidden)
                }

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

                val users = userService.getAllUsers(page, size)
                call.respond(HttpStatusCode.OK, users)
            }

            put("/{id}/block") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role !in listOf(UserRole.ADMIN.name, UserRole.LIBRARIAN.name)) {
                    return@put call.respond(HttpStatusCode.Forbidden)
                }

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))

                try {
                    val user = userService.blockUser(id)
                    call.respond(HttpStatusCode.OK, user)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            get("/{id}/penalties") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toInt()
                val role = principal?.payload?.getClaim("role")?.asString()

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))

                // Пользователь может видеть только свои штрафы, или ADMIN/LIBRARIAN могут видеть любые
                if (userId != id && role !in listOf(UserRole.ADMIN.name, UserRole.LIBRARIAN.name)) {
                    return@get call.respond(HttpStatusCode.Forbidden)
                }

                val penalties = userService.getUserPenalties(id)
                call.respond(HttpStatusCode.OK, penalties)
            }
        }
    }
}