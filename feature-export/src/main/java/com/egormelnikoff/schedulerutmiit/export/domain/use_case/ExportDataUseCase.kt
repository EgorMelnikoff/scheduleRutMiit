package com.egormelnikoff.schedulerutmiit.export.domain.use_case

import com.egormelnikoff.schedulerutmiit.export.data.repos.DataRepos
import kotlinx.serialization.json.Json

class ExportDataUseCase(
    private val dataRepos: DataRepos,
    private val json: Json
) {
    suspend operator fun invoke(): String {
        val data = dataRepos.getExportData()
        return json.encodeToString(data)
    }
}