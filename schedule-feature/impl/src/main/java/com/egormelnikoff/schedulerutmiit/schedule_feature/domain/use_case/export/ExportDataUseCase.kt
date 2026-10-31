package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.export

import android.net.Uri
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.file.FileExporter
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val serializeUseCase: SerializeUseCase,
    private val fileExporter: FileExporter
) {
    suspend operator fun invoke(uri: Uri) {
        val json = serializeUseCase()
        fileExporter.export(uri, json)
            ?: throw (Exception("Cannot export file"))
    }
}