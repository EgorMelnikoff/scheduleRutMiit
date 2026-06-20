package com.egormelnikoff.schedulerutmiit.export.domain.use_case

import android.net.Uri
import com.egormelnikoff.schedulerutmiit.core.common.logger.Logger
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.export.data.exporter.FileExporter
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val serializeUseCase: SerializeUseCase,
    private val fileExporter: FileExporter,
    private val logger: Logger
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> {
        return when (val json = serializeUseCase()) {
            is Result.Error -> Result.Error(json.typedError)
            is Result.Success -> {
                try {
                    fileExporter.export(uri, json.data)
                        ?: return Result.Error(TypedError.UnexpectedError(Exception("Cannot export file")))

                    Result.Success(Unit)
                } catch (e: Exception) {
                    logger.e("ExportDataUseCase", "Unexpected error", e)
                    Result.Error(TypedError.UnexpectedError(e))
                }
            }
        }
    }
}