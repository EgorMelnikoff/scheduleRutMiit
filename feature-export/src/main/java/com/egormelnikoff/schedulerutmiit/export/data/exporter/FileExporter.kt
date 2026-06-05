package com.egormelnikoff.schedulerutmiit.export.data.exporter

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FileExporter @Inject constructor(
    private val context: Context
) {
    suspend fun export(uri: Uri, data: String) = withContext(Dispatchers.IO) {
        return@withContext context.contentResolver.openOutputStream(uri)?.use {
            it.write(data.toByteArray())
        }
    }
}