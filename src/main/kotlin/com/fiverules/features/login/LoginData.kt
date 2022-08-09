package com.fiverules.features.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginReceiveData(
    val login: String,
    val password: String,
)

@Serializable
data class LoginResponseData(
    val token: String,
)