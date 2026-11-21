package com.egormelnikoff.schedulerutmiit.feature_schedule.glance.model

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class WidgetDataEventItem(
    val name: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDateTime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDateTime: LocalDateTime,
    val type: String?,
    val rooms: String? = null,
    val comment: String? = null,
    val tag: Int? = null
)