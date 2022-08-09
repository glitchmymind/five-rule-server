package com.fiverules.db.models

import com.fiverules.models.TaskDTO
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class RuleTask(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, RuleTask>(RuleTasks)

    var ruleId by RuleTasks.ruleId
    var taskId by RuleTasks.taskId
}

object RuleTasks : LongIdTable("rule_tasks") {

    val ruleId = long("ruleId")
    val taskId = long("taskId")

    fun insert(rId: Long, taskIdList: List<Long>) {
        transaction {
            taskIdList.forEach { tId ->
                RuleTasks.insert {
                    it[ruleId] = rId
                    it[taskId] = tId
                }
            }
        }
    }

    fun deleteByRuleId(rId: Long) {
        transaction {
            RuleTasks.deleteWhere { ruleId eq rId }
        }
    }

    fun deleteByTaskId(tId: Long) {
        transaction {
            RuleTasks.deleteWhere { taskId eq tId }
        }
    }

    fun getTaskForRule(rId: Long): List<TaskDTO> {
        return transaction {
            val complexJoin = Join(
                table = RuleTasks,
                otherTable = Tasks,
                onColumn = RuleTasks.taskId,
                otherColumn = Tasks.id,
                joinType = JoinType.INNER
            )

            complexJoin.select { ruleId eq rId }.map {
                TaskDTO(
                    id = it[Tasks.id].value,
                    name = it[Tasks.name],
                    description = it[Tasks.description],
                )
            }
        }
    }
}