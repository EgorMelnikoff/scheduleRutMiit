package com.egormelnikoff.schedulerutmiit.core.common.domain

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EventExtraData(
    val id: Long = 0,
    val eventId: Long,
    val scheduleId: Long = 0,
    val eventName: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dateTime: LocalDateTime?,
    val comment: String = "",
    val tag: Int = 0
)
