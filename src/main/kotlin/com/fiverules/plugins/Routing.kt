package com.fiverules.plugins

import com.fiverules.features.feed.feedRouting
import com.fiverules.features.login.loginRouting
import com.fiverules.features.registration.registerRouting
import com.fiverules.features.rules.rulesRouting
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

private const val API_V1 = "/api/v1"

fun Application.configureRouting() {

    routing {
        v1Routing()
    }
}

fun Route.v1Routing() {
    route(API_V1) {
        registerRouting()
        loginRouting()
        rulesRouting()
        feedRouting()

        // for test
        get {
            call.respond("Hello!!")
        }
    }
}
