package ru.clevertec.util

import dto.page.PageRequest
import io.ktor.server.application.ApplicationCall

fun ApplicationCall.getPageRequest(): PageRequest {
    val page = request.queryParameters["page"]?.toIntOrNull() ?: PageRequest.DEFAULT_PAGE
    val size = request.queryParameters["size"]?.toIntOrNull() ?: PageRequest.DEFAULT_SIZE
    
    return PageRequest(
        page = page.coerceAtLeast(1),
        size = size.coerceIn(1, PageRequest.MAX_SIZE)
    )
}