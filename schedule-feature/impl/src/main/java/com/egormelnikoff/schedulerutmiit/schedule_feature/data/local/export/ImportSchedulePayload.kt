package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export

import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.EventExtraDataImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.EventImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.NamedScheduleImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.ScheduleImport
import kotlinx.serialization.Serializable

@Serializable
data class ImportSchedulePayload(
    val namedSchedules: List<NamedScheduleImport>,
    val schedules: List<ScheduleImport>,
    val events: List<EventImport>,
    val eventsExtraData: List<EventExtraDataImport>
)