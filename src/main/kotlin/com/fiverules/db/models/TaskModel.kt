package com.fiverules.db.models

import com.fiverules.models.TaskDTO
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class Task(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, Task>(Tasks)

    var name by Tasks.name
    var description by Tasks.description
}

object Tasks : LongIdTable("tasks") {
    val name = varchar("name", 100)
    val description = varchar("description", 300)

    fun insert(task: TaskDTO) {
        transaction {
            Tasks.insert {
                it[name] = task.name
                it[description] = task.description
            }
        }
    }

    fun fetchTasks(): List<TaskDTO> {
        return transaction {
            Tasks.selectAll().map {
                TaskDTO(
                    id = it[Tasks.id].value,
                    name = it[name],
                    description = it[description],
                )
            }
        }
    }
}