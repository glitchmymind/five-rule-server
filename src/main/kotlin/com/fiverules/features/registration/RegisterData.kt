package com.fiverules.features.registration

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveData(
    val login: String,
    val email: String,
    val name: String,
    val password: String,
)

@Serializable
data class RegisterResponseData(
    val token: String,
)