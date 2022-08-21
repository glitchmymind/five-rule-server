package com.fiverules.features.authentication

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveData(
    val login: String,
    val email: String,
    val name: String,
    val password: String,
    val phone: String,
)

@Serializable
data class RegisterResponseData(
    val token: String,
)

@Serializable
data class CheckOptReceiveData(
    val otpCode: String,
    val otpId: String,
)


@Serializable
data class CheckOptResponseData(
    val otpId: String,
)

@Serializable
data class AuthResponseData(
    val token: String,
)

@Serializable
data class AuthReceiveData(
    val login: String,
    val password: String,
)

@Serializable
data class AuthPhoneReceiveData(
    val phone: String,
)