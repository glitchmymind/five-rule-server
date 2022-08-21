package com.fiverules.sessions

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

const val OTP_SESSION = "otp_session"

data class OtpSessions(
    val otpCode: String?,
    val otpId: String,
    val phoneNumber: String,
    val timeExpire: Long = MILLISECONDS.convert(300, TimeUnit.SECONDS),
    val creationTime: Long = System.currentTimeMillis()
) {

    fun isExpired() =
        (System.currentTimeMillis() - creationTime) > timeExpire
}