package com.fiverules.features.feed

import com.fiverules.db.models.Feeds
import com.fiverules.db.models.Rules
import com.fiverules.db.models.Users
import com.fiverules.models.FeedDTO
import com.fiverules.models.getEmptyRule
import com.fiverules.models.getEmptyUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class FeedInteractor {

    suspend fun getAllFeeds(call: ApplicationCall) {
        val feeds = transaction {
            Feeds.selectAll().map {
                FeedDTO(
                    id = it[Feeds.id].value,
                    text = it[Feeds.text],
                    time = it[Feeds.time],
                    user = Users.getUser(it[Feeds.userId]) ?: getEmptyUser(),
                    rule = Rules.getRule(it[Feeds.ruleId]) ?: getEmptyRule(),
                )
            }
        }
        call.respond(feeds)
    }

    suspend fun createFeed(call: ApplicationCall) {
        val data = call.receive<CreateFeedReceiveData>()
        Feeds.insert(data)
        call.respond(HttpStatusCode.OK, "OK")
    }
}