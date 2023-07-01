package com.fiverules.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource
import kotlin.math.min

object DbSettings {

    private const val dbTestProperties = "hikariStage.properties"
    private const val dbProdProperties = "hikariProd.properties"

    val dbMYSQL by lazy {
//        Database.connect("jdbc:mysql://localhost:3306/test", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "")
        Database.connect(postgresSource())
    }

    val dbPostgres by lazy {
        Database.connect(postgresSource())
    }

    val dbH2 by lazy {
        Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
    }

    private fun mysqlSource(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:mysql://localhost:3308"
        config.username = ""
        config.password = ""
        config.driverClassName = "com.mysql.cj.jdbc.Driver"

        return HikariDataSource(config)
    }

    private fun postgresSource(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = System.getenv("DB_URL")
        config.username = System.getenv("USER_NAME")
        config.password = System.getenv("PASSWORD")
        config.driverClassName = "org.postgresql.Driver"
        config.dataSource
        return HikariDataSource(config)
    }

    /**
     * Creates a HikariDataSource and returns it. If any exception is thrown, the operation is retried after x millis as
     * defined in the backoff sequence. If the sequence runs out of entries, the operation fails with the last
     * encountered exception.
     */
    tailrec fun createHikariDataSourceWithRetry(
        jdbcUrl: String,
        username: String,
        password: String,
        backoffSequenceMs: Iterator<Long> = defaultBackoffSequenceMs.iterator()
    ): HikariDataSource {
        try {
            val config = HikariConfig()
            config.jdbcUrl = jdbcUrl
            config.username = username
            config.password = password
            config.driverClassName = "org.postgresql.Driver"
            return HikariDataSource(config)
        } catch (ex: Exception) {
            if (!backoffSequenceMs.hasNext()) throw ex
        }
        val backoffMillis = backoffSequenceMs.next()
        Thread.sleep(backoffMillis)
        return createHikariDataSourceWithRetry(jdbcUrl, username, password, backoffSequenceMs)
    }

    val maxBackoffMs = 16000L
    val defaultBackoffSequenceMs = generateSequence(1000L) { min(it * 2, maxBackoffMs) }
}