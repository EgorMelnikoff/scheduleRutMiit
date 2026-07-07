package com.egormelnikoff.schedulerutmiit.core.common.domain

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class EventExtraData(
    val id: Long = 0,
    val eventId: Long,
    val scheduleId: Long = 0,
    val eventName: String?,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate?,
    val comment: String = "",
    val tag: Int = 0
)
