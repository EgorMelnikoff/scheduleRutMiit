package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.v2.ExportDataV2

interface ExportRepos {
    suspend fun getExportData(): ExportDataV2
    suspend fun importData(importSchedulePayload: ImportSchedulePayload)
}