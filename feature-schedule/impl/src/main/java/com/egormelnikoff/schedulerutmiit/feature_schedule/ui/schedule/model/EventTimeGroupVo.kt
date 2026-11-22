package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model

import androidx.compose.runtime.Immutable
import java.time.LocalTime

@Immutable
internal data class EventTimeGroupVo(
    val timeSlotName: String?,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val events: List<EventVo>
)
