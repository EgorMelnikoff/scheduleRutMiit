package com.egormelnikoff.schedulerutmiit.export.data.repos

import com.egormelnikoff.schedulerutmiit.export.dto.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.export.dto.v2.ExportDataV2

interface DataRepos {
    suspend fun getExportData(): ExportDataV2
    suspend fun importData(importSchedulePayload: ImportSchedulePayload)
}