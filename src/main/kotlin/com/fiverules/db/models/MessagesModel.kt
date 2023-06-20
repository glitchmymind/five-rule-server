package com.fiverules.db.models

import com.fiverules.models.MessageDTO
import com.fiverules.models.getEmptyFeed
import com.fiverules.models.getEmptyUser
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class Message(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, Message>(Messages)

    val feedId by Messages.feedId
    val text by Messages.text
    val userId by Messages.userId
    val time by Messages.time
}

object Messages : LongIdTable("messages") {
    val feedId = long("feedId")
    val text = varchar("text", 300)
    val userId = long("userId")
    val time = varchar("time", 50)

    fun insert(message: MessageDTO) {
        transaction {
            Messages.insert {
                it[feedId] = message.feed.id
                it[text] = message.text
                it[userId] = message.user.id
                it[time] = message.time
            }
        }
    }

    fun getMessages(feedId: Long): List<MessageDTO> {
        return transaction {
            Message.find { Messages.feedId eq feedId }.map {
                MessageDTO(
                    id = it.id.value,
                    feed = Feeds.getFeed(feedId) ?: getEmptyFeed(),
                    text = it.text,
                    user = Users.getUser(it.userId) ?: getEmptyUser(),
                    time = it.time,
                )
            }
        }
    }

}