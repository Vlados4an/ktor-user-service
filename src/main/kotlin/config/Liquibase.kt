package ru.clevertec.config

import io.ktor.server.application.*
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

fun Application.configureLiquibase() {
    val changelog = environment.config.property("liquibase.changelog").getString()

    config.hikariDataSource.connection.use { connection ->
        val database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(connection))
        val liquibase = Liquibase(
            changelog,
            ClassLoaderResourceAccessor(),
            database
        )
        liquibase.update("")
    }
}