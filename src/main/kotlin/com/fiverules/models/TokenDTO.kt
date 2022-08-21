package com.fiverules.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO(
    val id: Long = -1,
    val userId: Long,
    val token: String,
    val deviceId: String,
)