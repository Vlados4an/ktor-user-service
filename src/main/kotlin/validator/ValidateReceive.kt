package ru.clevertec.validator

import io.ktor.server.application.*
import io.ktor.server.request.*
import validator.Validatable

suspend inline fun <reified T : Any> ApplicationCall.validatedReceive(): T {
    val obj = this.receive<T>()

    if (obj is Validatable) {
        obj.validate()
    }

    return obj
}
