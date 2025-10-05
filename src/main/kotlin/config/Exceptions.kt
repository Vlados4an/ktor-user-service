package ru.clevertec.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.SerializationException
import org.valiktor.ConstraintViolationException
import ru.clevertec.exception.EntityNotFoundException

fun Application.configureExceptions() {
    install(StatusPages) {
        exception<EntityNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to (cause.message ?: "Entity not found"))
            )
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