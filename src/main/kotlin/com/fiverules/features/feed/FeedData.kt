package com.fiverules.features.feed

import kotlinx.serialization.Serializable

@Serializable
data class CreateFeedReceiveData(
    val text: String,
    val userId: Long,
    val ruleId: Long,
)