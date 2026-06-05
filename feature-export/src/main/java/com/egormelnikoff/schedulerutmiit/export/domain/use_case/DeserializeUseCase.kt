package com.egormelnikoff.schedulerutmiit.export.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.ExportData
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.export.data.repos.DataRepos
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DeserializeUseCase @Inject constructor(
    private val dataRepos: DataRepos,
    private val json: Json
) {
    suspend operator fun invoke(jsonString: String): Result<String> {
        return try {
            val data = json.decodeFromString<ExportData>(jsonString)
            validate(data)
            dataRepos.importData(data)
            Result.Success("")
        } catch (e: SerializationException) {
            Result.Error(TypedError.SerializationError(e))
        } catch (e: IllegalArgumentException) {
            Result.Error(TypedError.IllegalArgumentError(e))
        } catch (e: Exception) {
            Result.Error(TypedError.UnexpectedError(e))
        }
    }

    fun validate(data: ExportData) {
        require(data.version > 0) { "Invalid version" }
        require(data.namedSchedules.isNotEmpty()) { "Invalid data" }
    }
}
