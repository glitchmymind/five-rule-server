package com.fiverules.db.models

import com.fiverules.models.TaskDTO
import com.fiverules.models.TokenDTO
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class Token(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, Token>(Tokens)

    var userId by Tokens.userId
    var token by Tokens.token
    var deviceId by Tokens.deviceId
}

object Tokens : LongIdTable("tokens") {

    val userId = long("userId")
    val token = varchar("token", 300)
    val deviceId = varchar("deviceId", 50)

    fun insert(token: TokenDTO) {
        transaction {
            Tokens.insert {
                it[this.userId] = token.userId
                it[this.token] = token.token
                it[this.deviceId] = token.deviceId
            }
        }
    }

    fun delete(token: TokenDTO) {
        transaction {
            Tokens.deleteWhere { Tokens.token eq token.token }
        }
    }

    fun update(token: TokenDTO) {
        transaction {
            Tokens.update({ (Tokens.userId eq token.userId) and (Tokens.deviceId eq token.deviceId) }) {
                it[this.token] = token.token
            }
        }
    }

    fun getTokensByUserId(userId: Long): List<TokenDTO> {
        return transaction {
            Tokens.select { Tokens.userId eq userId }.map {
                TokenDTO(
                    id = it[Tokens.id].value,
                    userId = it[Tokens.userId],
                    token = it[Tokens.token],
                    deviceId = it[Tokens.deviceId],
                )
            }
        }
    }
}