package com.fiverules.features.authentication.otp

import com.fiverules.config.GlobalConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FlashCall(private val client: HttpClient) : OtpApi {

    override suspend fun sendOtp(phoneNumber: String, selfOtp: String?): TFACallResult {

        val response: HttpResponse = client.submitForm(
            url = "https://new-api.plusofon.ru",
            formParameters = Parameters.build {
                append("jsonrpc", "2.0")
                append("method", "TFA/call")
                append("id", "1")
                append("params[number]", phoneNumber)
            }
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${GlobalConfig.FLASH_CALL_API_TOKEN}")
            }
        }

        return Json.decodeFromString(response.bodyAsText())
    }

    override suspend fun checkOtpCode(otpId: String, otpCode: String): CheckResult {
        val response: HttpResponse = client.submitForm(
            url = "https://new-api.plusofon.ru",
            formParameters = Parameters.build {
                append("jsonrpc", "2.0")
                append("method", "TFA/check")
                append("id", "1")
                append("params[key]", otpId)
                append("params[pin]", otpCode)
            }
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${GlobalConfig.FLASH_CALL_API_TOKEN}")
            }
        }
        return Json.decodeFromString<TFACheckResult>(response.bodyAsText()).result.mapToResult()
    }
}

@Serializable
data class TFACallResult(
    val jsonrpc: String,
    val id: String,
    val result: String,
    val error: String?,
)

@Serializable
data class TFACheckResult(
    val jsonrpc: String,
    val id: String,
    val result: Int,
    val error: String?,
)

private fun Int.mapToResult(): CheckResult {
    return if (this == 1) {
        CheckResult.SUCCESS
    } else {
        CheckResult.ERROR
    }
}

enum class CheckResult {
    SUCCESS,
    ERROR
}