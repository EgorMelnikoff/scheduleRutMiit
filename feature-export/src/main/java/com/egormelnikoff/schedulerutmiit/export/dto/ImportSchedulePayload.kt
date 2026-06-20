package com.egormelnikoff.schedulerutmiit.export.dto

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import kotlinx.serialization.Serializable

@Serializable
data class ImportSchedulePayload(
    val namedSchedules: List<NamedSchedule>,
    val schedules: List<Schedule>,
    val events: List<Event>,
    val eventsExtraData: List<EventExtraData>
)