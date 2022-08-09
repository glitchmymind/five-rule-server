package com.fiverules.features.login

import com.fiverules.db.models.Users
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import java.util.UUID

object LoginInteractor {

    suspend fun login(call: ApplicationCall) {
        val loginData = call.receive<LoginReceiveData>()

        val user = Users.getUserByLogin(loginData.login)

        user?.let {
            if (user.password == loginData.password) {
                val token = UUID.randomUUID().toString()
                Users.updateUserToken(token)
                call.respond(LoginResponseData(token = token))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid password or login")
            }
        } ?: call.respond(HttpStatusCode.BadRequest, "Invalid password or login")
    }
}