package ru.clevertec.config

import io.ktor.server.application.*
import org.kodein.di.ktor.di
import ru.clevertec.modules.authModule

fun Application.configureDependencies() {
    di {
        import(authModule(this@configureDependencies))
    }
}
