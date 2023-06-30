package com.fiverules.features.rules

import com.fiverules.db.models.RuleTasks
import com.fiverules.db.models.Rules
import com.fiverules.db.models.Tasks
import com.fiverules.models.RuleDTO
import com.fiverules.models.TaskDTO
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

object RulesInteractor {

    suspend fun createNewRule(call: ApplicationCall) {
        val ruleData = call.receive<CreateRuleReceiveData>()
        Rules.insert(
            RuleDTO(
                id = -1,
                name = ruleData.name,
                description = ruleData.description,
            )
        )
        call.respond(HttpStatusCode.OK, "OK")
    }

    suspend fun deleteRule(call: ApplicationCall) {
        val data = call.receive<DeleteRuleReceiveData>()
        transaction {
            Rules.deleteWhere { Rules.id eq data.ruleId }
            RuleTasks.deleteWhere { RuleTasks.ruleId eq data.ruleId }
        }
        call.respond(HttpStatusCode.OK, "OK")
    }

    suspend fun createNewTask(call: ApplicationCall) {
        val taskData = call.receive<CreateTaskReceiveData>()
        Tasks.insert(
            TaskDTO(
                id = -1,
                name = taskData.name,
                description = taskData.description,
            )
        )
        call.respond(HttpStatusCode.OK, "OK")
    }

    suspend fun deleteTask(call: ApplicationCall) {
        val data = call.receive<DeleteTaskReceiveData>()
        transaction {
            Tasks.deleteWhere { Tasks.id eq data.taskId }
            RuleTasks.deleteWhere { RuleTasks.taskId eq data.taskId }
        }
        call.respond(HttpStatusCode.OK, "OK")
    }

    suspend fun addTasksToRule(call: ApplicationCall) {
        val data = call.receive<AddTasksToRuleReceiveData>()

        RuleTasks.insert(data.ruleId, data.taskIdList)
        call.respond(HttpStatusCode.OK, "OK")
    }
}