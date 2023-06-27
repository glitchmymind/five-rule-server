package com.fiverules.features.feed

import io.ktor.application.*
import io.ktor.routing.*

fun Route.feedRouting() {

    post("/create/feed") {
        FeedInteractor.createFeed(call)
    }

    get("/feeds") {
        FeedInteractor.getAllFeeds(call)
    }
}