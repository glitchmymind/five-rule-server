package com.fiverules.features.feed

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.feedRouting() {

    val feedInteractor: FeedInteractor by inject()

    post("/create/feed") {
        feedInteractor.createFeed(call)
    }

    get("/feeds") {
        feedInteractor.getAllFeeds(call)
    }
}