package com.fiverules.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Long,
    val login: String,
    val password: String,
    val name: String,
    val email: String,
    val avatar: String?,
    val rating: Int,
    val token: String, // JWT?
)

fun getEmptyUser(): UserDTO {
    return UserDTO(
        id = -1,
        login = "unknown",
        password = "",
        name = "unknown",
        email = "unknown",
        avatar = "",
        rating = 0,
        token = "",
    )
}

