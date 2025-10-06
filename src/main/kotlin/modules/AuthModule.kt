package ru.clevertec.modules

import io.ktor.server.application.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import service.auth.AuthService
import service.auth.AuthServiceImpl
import service.user.UserService
import service.user.UserServiceImpl

fun authModule(app: Application) = DI.Module("authModule") {
    val jwtSecret = app.environment.config.property("jwt.secret").getString()
    val jwtIssuer = app.environment.config.property("jwt.issuer").getString()
    bind<AuthService>() with singleton { AuthServiceImpl(jwtSecret, jwtIssuer) }
    bind<UserService>() with singleton { UserServiceImpl() }
}