package com.fiverules.models

import kotlinx.serialization.Serializable

@Serializable
data class FeedDTO(
    val id: Long,
    val text: String,
    val time: String,
    val user: UserDTO,
    val rule: RuleDTO,
)

fun getEmptyFeed(): FeedDTO {
    return FeedDTO(
        id = -1,
        text = "",
        time = "",
        user = getEmptyUser(),
        rule = getEmptyRule(),
    )
}