package ru.clevertec.repository

import dto.tracking.BookTrackingHistoryDto
import dto.tracking.CreateBookTrackingRequest
import dto.tracking.ReserveBookRequest
import dto.tracking.UpdateBookStatusRequest

interface BookTrackingRepository {
    fun create(req: CreateBookTrackingRequest): BookTrackingEntity
    fun findByBookId(bookId: Int): BookTrackingEntity?
    fun listAvailable(): List<BookTrackingEntity>
    fun updateStatus(bookId: Int, req: UpdateBookStatusRequest): BookTrackingEntity?
    fun reserve(bookId: Int, req: ReserveBookRequest): BookTrackingEntity?
    fun softDelete(bookId: Int): Boolean
    fun getHistory(bookId: Int): List<BookTrackingHistoryDto>
}