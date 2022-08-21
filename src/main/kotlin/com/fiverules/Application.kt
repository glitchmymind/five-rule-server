package com.fiverules

import com.fiverules.config.GlobalConfig
import com.fiverules.db.DbSettings
import com.fiverules.db.models.*
import com.fiverules.di.configureDependencyInjection
import com.fiverules.plugins.configureAuthentication
import com.fiverules.plugins.configureRouting
import com.fiverules.plugins.configureSerialization
import com.fiverules.sessions.OTP_SESSION
import com.fiverules.sessions.OtpSessions
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun main() {
    initDB()
    embeddedServer(Netty, port = GlobalConfig.PORT) {  // TODO: switch for debug and prod
        configureSerialization()
        install(Sessions) {
            val secretSignKey = hex("6819b57a326945c1968f45236589")
            cookie<OtpSessions>(OTP_SESSION, directorySessionStorage(File("build/.sessions"))) {
                cookie.maxAgeInSeconds = 30
                transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
            }
        }
        configureAuthentication()
        configureRouting()

        configureDependencyInjection()

        // TODO: добавить обработку ошибок!!!!

    }.start(wait = true)
}

private fun initDB() {

    // TODO: switch for debug and prod DB
    transaction(DbSettings.dbPostgres) {
        addLogger(StdOutSqlLogger)

        // TODO: нужно предусмотреть миграцию потом
        SchemaUtils.createMissingTablesAndColumns(
            Users,
            Rules,
            Messages,
            Feeds,
            Tasks,
            RuleTasks,
            Tokens,
        ) // создание всех таблиц если они отсутствуют
    }
}
