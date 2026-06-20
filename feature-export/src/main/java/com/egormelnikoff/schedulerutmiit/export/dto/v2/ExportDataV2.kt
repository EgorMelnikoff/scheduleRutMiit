package com.egormelnikoff.schedulerutmiit.export.dto.v2

import com.egormelnikoff.schedulerutmiit.export.dto.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.export.dto.VersionedExportData
import kotlinx.serialization.Serializable

@Serializable
data class ExportDataV2(
    val importSchedulePayload: ImportSchedulePayload
) : VersionedExportData {
    override val version: Int = 2

}