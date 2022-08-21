package com.fiverules.features.authentication.otp

interface OtpApi {
    suspend fun sendOtp(phoneNumber: String, selfOtp: String? = null): TFACallResult
    suspend fun checkOtpCode(otpId: String, otpCode: String): CheckResult
}