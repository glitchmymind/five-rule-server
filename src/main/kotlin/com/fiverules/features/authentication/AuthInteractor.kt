package com.fiverules.features.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fiverules.config.GlobalConfig
import com.fiverules.db.models.Tokens
import com.fiverules.db.models.User
import com.fiverules.db.models.Users
import com.fiverules.features.authentication.otp.CheckResult
import com.fiverules.features.authentication.otp.OtpApi
import com.fiverules.models.TokenDTO
import com.fiverules.plugins.AUTH_CLAIM_PHONE
import com.fiverules.plugins.AUTH_CLAIM_USER_ID
import com.fiverules.sessions.OTP_SESSION
import com.fiverules.sessions.OtpSessions
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class AuthInteractor(val otpService: OtpApi) {

    suspend fun registerNewUser(call: ApplicationCall) {
        val regData = call.receive<RegisterReceiveData>()

        val isUserExist = Users.isLoginExist(regData.login)

        if (isUserExist) {
            call.respond(HttpStatusCode.Conflict, "User already exist")
        } else {
            val newToken = generateToken(hashMapOf(AUTH_CLAIM_PHONE to regData.phone))

            transaction {
                User.new {
                    login = regData.login
                    password = regData.password
                    name = regData.name
                    email = regData.email
                    avatar = null
                    rating = 1
                }
            }
            call.respond(RegisterResponseData(token = newToken))
        }
    }

    suspend fun login(call: ApplicationCall) {
        val loginData = call.receive<AuthReceiveData>()

        val user = Users.getUserByLogin(loginData.login)

        user?.let {
            if (user.password == loginData.password) {
                val token = generateToken(hashMapOf(AUTH_CLAIM_PHONE to user.phoneNumber))
                Tokens.update(
                    TokenDTO(
                        userId = user.id,
                        token = token,
                        deviceId = ""
                    )
                )
                call.respond(AuthResponseData(token = token))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid password or login")
            }
        } ?: call.respond(HttpStatusCode.BadRequest, "Invalid password or login")
    }

    suspend fun authUserByPhone(call: ApplicationCall) {
        // https://github.com/google/libphonenumber for number formatting
        when (GlobalConfig.AUTH_TYPE) {
            "sms" -> authBySms(call)
            "flash_call" -> authByFlashCall(call)
        }
    }

    private suspend fun authBySms(call: ApplicationCall) {
        val regData = call.receive<RegisterReceiveData>()
        //  - generate uuid and reg code
        val otpId = UUID.randomUUID().toString()
        val otpCode = (100000..999999).random().toString()
        println("!!!!!! $otpCode")
        //  - save it to cache
        call.sessions.set(OtpSessions(otpId = otpId, otpCode = otpCode, phoneNumber = regData.phone))
        //  - send guid to client
        call.respond(CheckOptResponseData(otpId = otpId))
        //  TODO - send code by sms
    }

    private suspend fun authByFlashCall(call: ApplicationCall) {
        val regData = call.receive<AuthPhoneReceiveData>()

        // TODO: flash call api
        val result = otpService.sendOtp(phoneNumber = regData.phone)
        val otpId = result.result
        call.sessions.set(OtpSessions(otpId = otpId, otpCode = null, phoneNumber = regData.phone))
        call.respond(CheckOptResponseData(otpId = otpId))
    }

    suspend fun checkOtpCode(call: ApplicationCall) {
        when (GlobalConfig.AUTH_TYPE) {
            "sms" -> checkOtpCodeSms(call)
            "flash_call" -> checkOtpCodeFlashCall(call)
        }
    }

    private suspend fun checkOtpCodeFlashCall(call: ApplicationCall) {
        val otpData = call.receive<CheckOptReceiveData>()
        val otpCache = call.sessions.get<OtpSessions>()

        if (otpCache == null || otpCache.isExpired()) {
            call.sessions.clear(OTP_SESSION)
            call.respond(HttpStatusCode.BadRequest, "Otp code is expire!")
            return
        }

        if (otpData.otpId != otpCache.otpId) {
            call.respond(HttpStatusCode.BadRequest, "Invalid otp code")
            return
        }

        when (otpService.checkOtpCode(otpId = otpData.otpId, otpCode = otpData.otpCode)) {
            CheckResult.SUCCESS -> {
                val user = Users.getUserByPhone(otpCache.phoneNumber)
                var token = ""
                if (user == null) {
                    val userId = addNewUserByPhone(otpCache.phoneNumber)
                    token = generateToken(
                        hashMapOf(
                            AUTH_CLAIM_PHONE to otpCache.phoneNumber,
                            AUTH_CLAIM_USER_ID to userId.toString()
                        )
                    )
                    Tokens.insert(
                        TokenDTO(
                            userId = userId,
                            token = token,
                            deviceId = ""
                        )
                    )
                } else {
                    token = generateToken(
                        hashMapOf(
                            AUTH_CLAIM_PHONE to otpCache.phoneNumber,
                            AUTH_CLAIM_USER_ID to user.id.toString()
                        )
                    )
                    Tokens.update(
                        TokenDTO(
                            userId = user.id,
                            token = token,
                            deviceId = ""
                        )
                    )
                }
                call.respond(AuthResponseData(token = token))
            }

            else -> call.respond(HttpStatusCode.BadRequest, "Invalid otp code")
        }
    }

    private suspend fun checkOtpCodeSms(call: ApplicationCall) {
        call.respond(HttpStatusCode.BadRequest, "Validation method is unavailable")
    }

    private fun addNewUserByPhone(phoneNumber: String): Long {

        return transaction {
            Users.insertAndGetId {
                it[this.phoneNumber] = phoneNumber
                it[rating] = 1
                it[isBlocked] = false
            }
        }.value
    }

    private fun generateToken(args: HashMap<String, String?>): String {
        return JWT.create()
//            .withAudience(GlobalConfig.JWT_AUDIENCE)
            .withIssuer(GlobalConfig.JWT_ISSUER).apply {
                args.forEach { (key, value) ->
                    withClaim(key, value)
                }
            }
//            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(GlobalConfig.JWT_SECRET))
    }
}