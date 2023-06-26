package com.fiverules.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskDTO(
    val id: Long,
    val name: String,
    val description: String = "",
)
