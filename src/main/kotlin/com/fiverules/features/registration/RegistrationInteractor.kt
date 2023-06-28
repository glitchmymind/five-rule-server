package com.fiverules.features.registration

import com.fiverules.db.models.User
import com.fiverules.db.models.Users
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object RegistrationInteractor {

    // TODO: use OPT registration and save otp ID with session in-memory cache https://ktor.io/docs/sessions.html
    suspend fun registerNewUser(call: ApplicationCall) {
        val regData = call.receive<RegisterReceiveData>()

        val isUserExist = Users.isUserExist(regData.login)

        if (isUserExist) {
            call.respond(HttpStatusCode.Conflict, "User already exist")
        } else {
            val newToken = UUID.randomUUID().toString() // TODO: JWT Bearer https://ktor.io/docs/authentication.html

            transaction {
                User.new {
                    login = regData.login
                    password = regData.password
                    name = regData.name
                    email = regData.email
                    avatar = null
                    rating = 1
                    token = newToken
                }
            }
            call.respond(RegisterResponseData(token = newToken))
        }
    }
}