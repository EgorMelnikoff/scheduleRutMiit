package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.v2

import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.VersionedExportData
import kotlinx.serialization.Serializable

@Serializable
data class ExportDataV2(
    val importSchedulePayload: ImportSchedulePayload
) : VersionedExportData {
    override val version: Int = 2

}