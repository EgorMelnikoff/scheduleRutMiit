package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.export

import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.ExportEnvelope
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.v1.ExportDataV1
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.v2.ExportDataV2
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.ExportRepos
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DeserializeUseCase @Inject constructor(
    private val exportRepos: ExportRepos,
    private val json: Json,
) {
    suspend operator fun invoke(jsonString: String) {
        val payload = deserializeToImportPayload(jsonString)
        validate(payload)
        exportRepos.importData(payload)
    }

    private fun deserializeToImportPayload(jsonString: String): ImportSchedulePayload {
        val exportEnvelope = json.decodeFromString<ExportEnvelope>(jsonString)
        return when (exportEnvelope.version) {
            1 -> {
                val data = json.decodeFromString<ExportDataV1>(jsonString)
                data.toImportPayload()
            }

            2 -> {
                val data = json.decodeFromString<ExportDataV2>(jsonString)
                data.importSchedulePayload
            }

            else -> throw IllegalArgumentException("Unsupported export version: ${exportEnvelope.version}")
        }
    }

    private fun validate(payload: ImportSchedulePayload) {
        require(payload.namedSchedules.isNotEmpty()) { "namedSchedules must not be empty" }
    }
}
