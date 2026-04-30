package com.egormelnikoff.schedulerutmiit.export.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.ExportData

interface DataRepos {
    suspend fun getExportData(): ExportData
    suspend fun importData(data: ExportData)
}