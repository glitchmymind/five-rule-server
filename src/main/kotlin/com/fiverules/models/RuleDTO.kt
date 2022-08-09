package com.fiverules.models

import kotlinx.serialization.Serializable

@Serializable
data class RuleDTO(
    val id: Long,
    val name: String,
    val description: String = "",
    val taskList: List<TaskDTO> = emptyList()
)

fun getEmptyRule(): RuleDTO {
    return RuleDTO(
        id = -1,
        name = "unknown",
    )
}