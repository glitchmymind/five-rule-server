package com.fiverules.db.models

import com.fiverules.models.RuleDTO
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class Rule(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, Rule>(Rules)

    var name by Rules.name
    var description by Rules.description
}

object Rules : LongIdTable("rules") {
    val name = varchar("name", 100)
    val description = varchar("description", 300)

    fun insert(rule: RuleDTO) {
        transaction {
            Rules.insert {
                it[name] = rule.name
                it[description] = rule.description
            }
        }
    }

    fun getRule(ruleId: Long): RuleDTO? {
        return transaction {
            val rulesModel = Rule.findById(ruleId)
            if (rulesModel != null) {
                RuleDTO(
                    id = rulesModel.id.value,
                    name = rulesModel.name,
                    description = rulesModel.description,
                    taskList = RuleTasks.getTaskForRule(ruleId) // TODO: оптимизировать запрос
                )
            } else {
                null
            }
        }
    }

    fun fetchRules(): List<RuleDTO> {
        return transaction {
            Rules.selectAll().map {
                RuleDTO(
                    id = it[Rules.id].value,
                    name = it[name],
                    description = it[description],
                    taskList = RuleTasks.getTaskForRule(it[Rules.id].value) // TODO: оптимизировать запрос
                )
            }
        }
    }
}