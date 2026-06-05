package com.egormelnikoff.schedulerutmiit.export.domain.use_case

import android.net.Uri
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.export.data.importer.FileImporter
import javax.inject.Inject

class ImportDataUseCase @Inject constructor(
    private val deserializeUseCase: DeserializeUseCase,
    private val fileImporter: FileImporter
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> {
        return try {
            val jsonString = fileImporter.import(uri) ?: return Result.Error(
                TypedError.UnexpectedError(Exception("Cannot open file"))
            )

            when (val result = deserializeUseCase(jsonString)) {
                is Result.Error -> Result.Error(result.typedError)
                is Result.Success -> Result.Success(Unit)
            }

        } catch (e: Exception) {
            Result.Error(TypedError.UnexpectedError(e))
        }
    }
}
