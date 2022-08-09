package com.fiverules.features.login

import io.ktor.application.*
import io.ktor.routing.*

fun Route.loginRouting() {
    post("/login") {
        LoginInteractor.login(call)
    }
}