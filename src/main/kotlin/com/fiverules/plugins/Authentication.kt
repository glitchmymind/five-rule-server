package com.fiverules.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fiverules.config.GlobalConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

const val AUTH_CONFIG = "auth-jwt"
const val AUTH_CLAIM_PHONE = "phone"
const val AUTH_CLAIM_USER_ID = "userId"

fun Application.configureAuthentication() {

    install(Authentication) {
        jwt(AUTH_CONFIG) {
            realm = GlobalConfig.JWT_REALM
            verifier(
                JWT
                    .require(Algorithm.HMAC256(GlobalConfig.JWT_SECRET))
//                    .withAudience(GlobalConfig.JWT_AUDIENCE)
                    .withIssuer(GlobalConfig.JWT_ISSUER)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(AUTH_CLAIM_PHONE).asString() != ""
                    && credential.payload.getClaim(AUTH_CLAIM_USER_ID).asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}