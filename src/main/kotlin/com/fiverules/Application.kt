package com.fiverules

import com.fiverules.db.DbSettings
import com.fiverules.db.models.*
import com.fiverules.plugins.configureRouting
import com.fiverules.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    initDB()
    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {  // TODO: switch for debug and prod
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}

private fun initDB() {

    // TODO: switch for debug and prod DB
    transaction(DbSettings.dbPostgres) {
        addLogger(StdOutSqlLogger)

        // TODO: нужно предусмотреть миграцию  потом
        SchemaUtils.createMissingTablesAndColumns(Users, Rules, Messages, Feeds, Tasks, RuleTasks) // создание всех таблиц если они отсутствуют
    }
}
