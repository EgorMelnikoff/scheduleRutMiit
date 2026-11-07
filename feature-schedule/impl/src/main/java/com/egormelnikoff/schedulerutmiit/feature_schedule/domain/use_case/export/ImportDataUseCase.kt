package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.export

import android.net.Uri
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.file.FileImporter
import javax.inject.Inject

class ImportDataUseCase @Inject constructor(
    private val deserializeUseCase: DeserializeUseCase,
    private val fileImporter: FileImporter
) {
    suspend operator fun invoke(uri: Uri) {
        val jsonString = fileImporter.import(uri) ?: throw Exception("Cannot open file")
        deserializeUseCase(jsonString)
    }
}
