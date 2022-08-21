package com.fiverules.cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

interface Cache<T> {
    fun get(key: String): T?
    fun put(key: String, value: T)
}

class TimedCache<T>() : Cache<T> {
    private var cacheTimeValidityInMillis: Long = 0
    private val hashMap = ConcurrentHashMap<String, TimedEntry<T>>()

    companion object {
        fun <T> expiringEvery(duration: Long, timeUnit: TimeUnit) =
            TimedCache<T>().apply {
                cacheTimeValidityInMillis = MILLISECONDS.convert(duration, timeUnit)
            }
    }

    override fun get(key: String): T? {
        val timedEntry = hashMap[key]
        if (timedEntry == null || timedEntry.isExpired()) {
            hashMap.remove(key)
            return null
        }

        return timedEntry.value
    }

    override fun put(key: String, value: T) {
        hashMap[key] = TimedEntry(value, cacheTimeValidityInMillis)
    }

    data class TimedEntry<T>(val value: T, val maxDurationInMillis: Long) {
        private val creationTime: Long = now()

        fun isExpired() = (now() - creationTime) > maxDurationInMillis

        private fun now() = System.currentTimeMillis()
    }
}