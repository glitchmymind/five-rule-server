package com.fiverules.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO(
    val id: Long,
    val feed: FeedDTO,
    val text: String,
    val user: UserDTO,
    val time: String,
)
