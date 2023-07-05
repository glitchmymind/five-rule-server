package com.fiverules.config

// check environments in compile time
object GlobalConfig {
    val PORT: Int = System.getenv("PORT").toInt()
    val DB_URL: String = System.getenv("DB_URL")
    val USER_NAME: String = System.getenv("USER_NAME")
    val PASSWORD: String = System.getenv("PASSWORD")
    val JWT_SECRET: String = System.getenv("JWT_SECRET")
    val AUTH_TYPE: String = System.getenv("AUTH_TYPE")
    val JWT_ISSUER: String = System.getenv("JWT_ISSUER")
    val JWT_REALM: String = System.getenv("JWT_REALM")
    val JWT_AUDIENCE: String = System.getenv("JWT_AUDIENCE")

    val FLASH_CALL_API_TOKEN: String = System.getenv("FLASH_CALL_API_TOKEN")
}