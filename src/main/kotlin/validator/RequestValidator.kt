package util

import io.ktor.server.application.*

fun ApplicationCall.getIntParamOrBadRequest(name: String): Int {
    val value = parameters[name]?.toIntOrNull()
    return value ?: throw IllegalArgumentException("Parameter '$name' is missing")
}
