package com.egormelnikoff.schedulerutmiit.export.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.logger.Logger
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.export.data.repos.DataRepos
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SerializeUseCase @Inject constructor(
    private val dataRepos: DataRepos,
    private val json: Json,
    private val logger: Logger
) {
    suspend operator fun invoke(): Result<String> {
        val data = dataRepos.getExportData()
        return try {
            Result.Success(json.encodeToString(data))
        } catch (e: SerializationException) {
            logger.e("SerializeUseCase", "Serialization error", e)
            Result.Error(TypedError.SerializationError(e))
        } catch (e: Exception) {
            logger.e("SerializeUseCase", "Unexpected error", e)
            Result.Error(TypedError.UnexpectedError(e))
        }
    }
}