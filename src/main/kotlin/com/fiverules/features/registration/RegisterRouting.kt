package com.fiverules.features.registration

import io.ktor.application.*
import io.ktor.routing.*

fun Route.registerRouting() {

        post("/register") {
            RegistrationInteractor.registerNewUser(call)
        }
}