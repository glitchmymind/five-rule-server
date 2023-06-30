package com.fiverules.features.rules

import com.fiverules.models.RuleDTO
import kotlinx.serialization.Serializable

@Serializable
data class CreateRuleReceiveData(
    val name: String,
    val description: String,
)

@Serializable
data class CreateTaskReceiveData(
    val name: String,
    val description: String,
)

@Serializable
data class DeleteRuleReceiveData(
    val ruleId: Long,
)

@Serializable
data class DeleteTaskReceiveData(
    val taskId: Long,
)

@Serializable
data class AddTasksToRuleReceiveData(
    val ruleId: Long,
    val taskIdList: List<Long>,
)

@Serializable
data class RuleResponseData(
    val rules: List<RuleDTO>,
)