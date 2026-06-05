package com.egormelnikoff.schedulerutmiit.export.data.importer

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FileImporter @Inject constructor(
    private val context: Context
) {
    suspend fun import(uri: Uri): String? = withContext(Dispatchers.IO) {
        return@withContext context.contentResolver.openInputStream(uri)?.use { input ->
            input.bufferedReader(Charsets.UTF_8).readText()
        }
    }
}