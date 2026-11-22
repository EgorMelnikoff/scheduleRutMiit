package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

import java.time.LocalDate

internal data class EventExtraData(
    val id: Long = 0,
    val eventId: Long,
    val scheduleId: Long,
    val eventName: String?,
    val date: LocalDate?,
    val comment: String = "",
    val tag: Int = 0
)
