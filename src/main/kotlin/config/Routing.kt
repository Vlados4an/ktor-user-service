package ru.clevertec.config

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.clevertec.routes.authRoutes
import ru.clevertec.routes.userRoutes

fun Application.configureRouting() {
    routing {
        authRoutes()
        userRoutes()
    }
}
