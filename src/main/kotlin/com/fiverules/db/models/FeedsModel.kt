package com.fiverules.db.models

import com.fiverules.features.feed.CreateFeedReceiveData
import com.fiverules.models.FeedDTO
import com.fiverules.models.getEmptyRule
import com.fiverules.models.getEmptyUser
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class Feed(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, Feed>(Feeds)

    val text by Feeds.text
    val time by Feeds.time
    val userId by Feeds.userId
    val ruleId by Feeds.ruleId
}

object Feeds : LongIdTable("feeds") {
    val text = varchar("text", 300)
    val time = varchar("time", 50)
    val userId = long("userId")
    val ruleId = long("ruleId")

    fun insert(feed: CreateFeedReceiveData) {
        transaction {
            Feeds.insert {
                it[text] = feed.text
                it[time] = System.currentTimeMillis().toString()
                it[userId] = feed.userId
                it[ruleId] = feed.ruleId
            }
        }
    }

    fun getFeed(feedId: Long): FeedDTO? {
        return transaction {
            val feedModel = Feed.findById(feedId)
            if (feedModel != null) {
                FeedDTO(
                    id = feedModel.id.value,
                    text = feedModel.text,
                    time = feedModel.time,
                    user = Users.getUser(feedModel.userId) ?: getEmptyUser(),
                    rule = Rules.getRule(feedModel.ruleId) ?: getEmptyRule(),
                )
            } else {
                null
            }
        }
    }
}