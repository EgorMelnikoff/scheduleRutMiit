package com.egormelnikoff.schedulerutmiit.core.common.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class TimeProvider @Inject constructor() {
    private fun tickerFlow(interval: Duration) = flow {
        while (true) {
            emit(Unit)
            delay(interval)
        }
    }

    val currentMinute: Flow<LocalDateTime> =
        tickerFlow(1.minutes)
            .map { LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) }
            .distinctUntilChanged()

    val currentHour: Flow<LocalDateTime> =
        currentMinute
            .map { it.truncatedTo(ChronoUnit.HOURS) }
            .distinctUntilChanged()
}