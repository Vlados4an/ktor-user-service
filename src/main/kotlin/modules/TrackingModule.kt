package ru.clevertec.modules

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import ru.clevertec.repository.BookTrackingRepository
import ru.clevertec.repository.BookTrackingRepositoryImpl

val trackingModule = DI.Module("trackingModule") {
    bind<BookTrackingRepository>() with singleton { BookTrackingRepositoryImpl() }
    bind<BookTrackingService>() with singleton { BookTrackingServiceImpl(instance()) }
}