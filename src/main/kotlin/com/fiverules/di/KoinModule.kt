package com.fiverules.di

import com.fiverules.features.authentication.AuthInteractor
import com.fiverules.features.authentication.otp.FlashCall
import com.fiverules.features.authentication.otp.OtpApi
import com.fiverules.features.feed.FeedInteractor
import com.fiverules.features.rules.RulesInteractor
import io.ktor.client.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

fun Application.configureDependencyInjection() {

    install(Koin) {
        SLF4JLogger()
        modules(
            AuthModule,
            NetworkModule,
            FeedModule,
            RulesModule,
        )
    }
}

val AuthModule = module {
    singleOf(::FlashCall) { bind<OtpApi>() }
    singleOf(::AuthInteractor)
}

val NetworkModule = module {
    single { HttpClient(CIO) }
}

val FeedModule = module {
    singleOf(::FeedInteractor)
}

val RulesModule = module {
    singleOf(::RulesInteractor)
}