package com.egormelnikoff.schedulerutmiit.export.dto.v1

import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.export.dto.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.export.dto.VersionedExportData
import com.egormelnikoff.schedulerutmiit.export.dto.v1.data.EventV1
import kotlinx.serialization.Serializable

@Serializable
data class ExportDataV1(
    val namedSchedules: List<NamedSchedule>,
    val schedules: List<Schedule>,
    val events: List<EventV1>,
    val eventsExtraData: List<EventExtraData>
) : VersionedExportData {
    override val version: Int = 1

    fun toImportPayload() = ImportSchedulePayload(
        namedSchedules, schedules, events.map { it.toEvent() }, eventsExtraData
    )

}