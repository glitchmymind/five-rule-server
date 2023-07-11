package com.fiverules.db.models

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Subscriptions : LongIdTable("subscriptions") {

    private val userId = long("userId")
    private val expireData = timestamp("expireData")

    fun insert(userId: Long) {
        transaction {
            Subscriptions.insert {
                it[this.userId] = userId
            }
        }
    }

    fun update(userId: Long, newDate: String) {
        transaction {
            Subscriptions.update {
                Subscriptions.userId eq userId
            }
        }
    }

    fun delete(userId: Long) {
        transaction {
            Subscriptions.deleteWhere { Subscriptions.userId eq userId }
        }
    }
}