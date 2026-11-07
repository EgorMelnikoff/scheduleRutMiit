package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class EventExtraDataImport(
    val id: Long = 0,
    val eventId: Long,
    val scheduleId: Long,
    val eventName: String?,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate?,
    val comment: String = "",
    val tag: Int = 0
)
