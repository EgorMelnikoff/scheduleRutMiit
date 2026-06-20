package com.egormelnikoff.schedulerutmiit.export.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.logger.Logger
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.export.data.repos.DataRepos
import com.egormelnikoff.schedulerutmiit.export.dto.ExportEnvelope
import com.egormelnikoff.schedulerutmiit.export.dto.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.export.dto.v1.ExportDataV1
import com.egormelnikoff.schedulerutmiit.export.dto.v2.ExportDataV2
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DeserializeUseCase @Inject constructor(
    private val dataRepos: DataRepos,
    private val json: Json,
    private val logger: Logger
) {
    suspend operator fun invoke(jsonString: String): Result<String> {
        return try {
            val payload = deserializeToImportPayload(jsonString)
            validate(payload)
            dataRepos.importData(payload)
            Result.Success("")
        } catch (e: SerializationException) {
            logger.e("DeserializeUseCase", "Serialization error", e)
            Result.Error(TypedError.SerializationError(e))
        } catch (e: IllegalArgumentException) {
            logger.e("DeserializeUseCase", "Illegal argument error", e)
            Result.Error(TypedError.IllegalArgumentError(e))
        } catch (e: Exception) {
            logger.e("DeserializeUseCase", "Unexpected error", e)
            Result.Error(TypedError.UnexpectedError(e))
        }
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
