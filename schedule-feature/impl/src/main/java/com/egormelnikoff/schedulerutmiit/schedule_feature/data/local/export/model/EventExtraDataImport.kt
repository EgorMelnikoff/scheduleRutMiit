package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model

import com.egormelnikoff.schedulerutmiit.core.common.utils.serializers.LocalDateSerializer
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
