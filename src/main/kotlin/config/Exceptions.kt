package ru.clevertec.config

import exception.InsufficientPermissionsException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.SerializationException
import org.valiktor.ConstraintViolationException
import ru.clevertec.exception.EntityNotFoundException
import ru.clevertec.exception.InvalidCredentialsException
import ru.clevertec.exception.InvalidTokenException
import ru.clevertec.exception.NotAuthorizedException
import ru.clevertec.exception.UserAlreadyExistsException
import ru.clevertec.exception.UserBlockedException

fun Application.configureExceptions() {
    install(StatusPages) {
        exception<EntityNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to (cause.message ?: "Entity not found"))
            )
        }

        exception<InvalidCredentialsException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to cause.message))
        }

        exception<InvalidTokenException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to cause.message))
        }

        exception<UserAlreadyExistsException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to cause.message))
        }

        exception<UserBlockedException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to cause.message))
        }

        exception<NotAuthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to cause.message))
        }

        exception<InsufficientPermissionsException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to cause.message))
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Internal server error"))
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (cause.message ?: "Invalid request")))
        }

        exception<SerializationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid JSON: ${cause.message}"))
        }

        exception<ConstraintViolationException> { call, cause ->
            val errors = cause.constraintViolations.groupBy { it.property }
                .mapValues { entry ->
                    entry.value.map { v -> v.constraint.name }
                }

            call.respond(HttpStatusCode.BadRequest, mapOf("errors" to errors))
        }
    }
}