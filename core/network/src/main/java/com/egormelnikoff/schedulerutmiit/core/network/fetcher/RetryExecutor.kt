package com.egormelnikoff.schedulerutmiit.core.network.fetcher

import android.util.Log
import kotlinx.coroutines.delay
import okio.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class RetryExecutor @Inject constructor() {
    suspend fun <T> execute(
        tag: String,
        maxAttempts: Int = 3,
        block: suspend () -> T
    ): T {
        require(maxAttempts > 0) {
            "maxAttempts must be greater than 0"
        }

        repeat(maxAttempts) { attempt ->
            try {
                return block()
            } catch (e: SocketTimeoutException) {
                Log.w(tag, "Timeout (${attempt + 1}/$maxAttempts)", e)

                if (attempt == maxAttempts - 1) throw e
            } catch (e: IOException) {
                Log.w(tag, "Network error (${attempt + 1}/$maxAttempts)", e)

                if (attempt == maxAttempts - 1) throw e
            }

            delay(backoff(attempt).milliseconds)
        }

        error("Unreachable")
    }

    private fun backoff(attempt: Int): Long {
        val exponential = (INITIAL_BACKOFF_MS * 2.0.pow(attempt))
            .toLong()
            .coerceAtMost(MAX_BACKOFF_MS)

        val jitter = (exponential * JITTER_FACTOR).toLong()
        return exponential + Random.nextLong(-jitter, jitter + 1)
    }

    companion object {
        private const val INITIAL_BACKOFF_MS = 500L
        private const val MAX_BACKOFF_MS = 8_000L
        private const val JITTER_FACTOR = 0.2
    }

}