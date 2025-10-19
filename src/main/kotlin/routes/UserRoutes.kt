package ru.clevertec.routes

import dto.auth.CreatePenaltyRequest
import dto.user.UpdateUserRequest
import exception.InsufficientPermissionsException
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.enums.UserRole
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import ru.clevertec.util.getPageRequest
import ru.clevertec.validator.getIntParamOrBadRequest
import ru.clevertec.validator.validatedReceive
import service.user.UserService

fun Route.userRoutes() {

    val userService by closestDI().instance<UserService>()

    get("/api/v1/users/{id}/email") {
        val id = call.getIntParamOrBadRequest("id")
        val email = userService.getUserEmail(id)
        call.respond(HttpStatusCode.OK, email)
    }

    authenticate("auth-jwt") {
        route("/api/v1/users") {

            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toInt()
                    ?: throw InsufficientPermissionsException("Unauthorized")

                val user = userService.getCurrentUser(userId)
                call.respond(HttpStatusCode.OK, user)
            }

            put("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toInt()
                    ?: throw InsufficientPermissionsException("Unauthorized")

                val request = call.validatedReceive<UpdateUserRequest>()
                val user = userService.updateCurrentUser(userId, request)
                call.respond(HttpStatusCode.OK, user)
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != UserRole.ADMIN.name) {
                    throw InsufficientPermissionsException("Admin access required")
                }

                val id = call.getIntParamOrBadRequest("id")

                val user = userService.getUserById(id)
                call.respond(HttpStatusCode.OK, user)
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != UserRole.ADMIN.name) {
                    return@get call.respond(HttpStatusCode.Forbidden)
                }

                val pageRequest = call.getPageRequest()

                val users = userService.getAllUsers(pageRequest)
                call.respond(HttpStatusCode.OK, users)
            }

            put("/{id}/block") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role !in listOf(UserRole.ADMIN.name, UserRole.LIBRARIAN.name)) {
                    throw InsufficientPermissionsException("Admin or Librarian access required")
                }

                val id = call.getIntParamOrBadRequest("id")

                val user = userService.blockUser(id)
                call.respond(HttpStatusCode.OK, user)
            }

            get("/{id}/penalties") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toInt()
                val role = principal?.payload?.getClaim("role")?.asString()

                val id = call.getIntParamOrBadRequest("id")

                if (userId != id && role !in listOf(UserRole.ADMIN.name, UserRole.LIBRARIAN.name)) {
                    throw InsufficientPermissionsException("Admin or Librarian access required")
                }

                val penalties = userService.getUserPenalties(id)
                call.respond(HttpStatusCode.OK, penalties)
            }

            post("/penalties") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role !in listOf(UserRole.ADMIN.name, UserRole.LIBRARIAN.name)) {
                    throw InsufficientPermissionsException("Admin or Librarian access required")
                }

                val request = call.validatedReceive<CreatePenaltyRequest>()
                val penalty = userService.createPenalty(request)
                call.respond(HttpStatusCode.Created, penalty)
            }
        }
    }
}