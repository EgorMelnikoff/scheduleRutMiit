package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos

import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.v2.ExportDataV2

interface ExportRepos {
    suspend fun getExportData(): ExportDataV2
    suspend fun importData(importSchedulePayload: ImportSchedulePayload)
}