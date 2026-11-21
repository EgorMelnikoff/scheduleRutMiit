package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.v2

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.VersionedExportData
import kotlinx.serialization.Serializable

@Serializable
data class ExportDataV2(
    val importSchedulePayload: ImportSchedulePayload
) : VersionedExportData {
    override val version: Int = 2

}