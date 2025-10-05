package ru.clevertec.modules

import io.ktor.server.application.ApplicationEnvironment
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun createAppModule(environment: ApplicationEnvironment) = DI.Module("appModule") {
    bind<ApplicationEnvironment>() with singleton { environment }
}