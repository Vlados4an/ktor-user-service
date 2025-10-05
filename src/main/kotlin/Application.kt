package ru.clevertec

import config.configureDatabase
import config.configureSerialization
import io.ktor.server.application.*
import ru.clevertec.config.configureDependencies
import ru.clevertec.config.configureExceptions
import ru.clevertec.config.configureLiquibase
import ru.clevertec.config.configureRouting

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabase()
    configureLiquibase()
    configureDependencies()
    configureRouting()
    configureExceptions()
    configureSerialization()
    configureAuth()
}
