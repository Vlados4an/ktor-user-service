package ru.clevertec.config

import ru.clevertec.modules.createAppModule
import io.ktor.server.application.*
import org.kodein.di.ktor.di
import ru.clevertec.modules.kafkaModule
import ru.clevertec.modules.authModule

fun Application.configureDependencies() {
    di {
        import(createAppModule(environment))
        import(kafkaModule)
        import(authModule)
    }
}
