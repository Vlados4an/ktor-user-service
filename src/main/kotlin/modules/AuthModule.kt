package ru.clevertec.modules

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import ru.clevertec.repository.BookTrackingRepository
import ru.clevertec.repository.BookTrackingRepositoryImpl

val authModule = DI.Module("trackingModule") {
    bind<BookTrackingRepository>() with singleton { BookTrackingRepositoryImpl() }
}