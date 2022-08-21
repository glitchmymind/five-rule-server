package com.fiverules.plugins

import com.fiverules.features.feed.feedRouting
import com.fiverules.features.authentication.authRouting
import com.fiverules.features.rules.rulesRouting
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val API_V1 = "/api/v1"


fun Application.configureRouting() {

    routing {
        v1Routing()
    }
}

fun Route.v1Routing() {
    route(API_V1) {
        authRouting()
        authenticate(AUTH_CONFIG) {
            rulesRouting()
            feedRouting()
        }

        // for test
        get {
            call.respond("Hello!!")
        }
    }
}
