package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.export

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.ExportRepos
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SerializeUseCase @Inject constructor(
    private val exportRepos: ExportRepos,
    private val json: Json,
) {
    suspend operator fun invoke(): String {
        val data = exportRepos.getExportData()
        return json.encodeToString(data)
    }
}