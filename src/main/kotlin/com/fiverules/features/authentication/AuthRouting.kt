package com.fiverules.features.authentication

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRouting() {

    val authInteractor: AuthInteractor by inject()

    post("/register") {
        authInteractor.registerNewUser(call)
    }

    route("/auth") {
        post("/phone") {
            authInteractor.authUserByPhone(call)
        }

        post("/check_otp") {
            authInteractor.checkOtpCode(call)
        }

        post("/login") {

        }

        post("/email") {

        }
    }
}