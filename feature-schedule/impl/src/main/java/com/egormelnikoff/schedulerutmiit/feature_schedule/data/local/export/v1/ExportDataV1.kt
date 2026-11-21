package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.v1

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.VersionedExportData
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.EventExtraDataImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.NamedScheduleImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.ScheduleImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.v1.data.EventV1
import kotlinx.serialization.Serializable

@Serializable
data class ExportDataV1(
    val namedSchedules: List<NamedScheduleImport>,
    val schedules: List<ScheduleImport>,
    val events: List<EventV1>,
    val eventsExtraData: List<EventExtraDataImport>
) : VersionedExportData {
    override val version: Int = 1

    fun toImportPayload() = ImportSchedulePayload(
        namedSchedules, schedules, events.map { it.toEvent() }, eventsExtraData
    )

}