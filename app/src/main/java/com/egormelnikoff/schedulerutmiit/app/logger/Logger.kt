package com.egormelnikoff.schedulerutmiit.app.logger

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.AppConst.DEVELOPER_EMAIL
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import javax.inject.Inject

class Logger @Inject constructor(
    private val context: Context,
    private val resourcesManager: ResourcesManager
) {
    companion object {
        private const val LOG_FILE_NAME = "app_logs.txt"
        private const val MAX_LOG_SIZE_BYTES = 1024 * 1024L
    }

    fun sendLogsFile() {
        val logFile = getLogFile()

        if (!logFile.exists() || logFile.length() == 0L) {
            Toast.makeText(
                context,
                resourcesManager.getString(R.string.logs_not_found),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            val fileUri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                logFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(DEVELOPER_EMAIL))
                putExtra(Intent.EXTRA_SUBJECT, "logs: ${context.packageName}")
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(
                    intent,
                    resourcesManager.getString(R.string.send_logs)
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

        } catch (e: Exception) {
            Log.e(
                "FileLogger",
                resourcesManager.getString(R.string.error_sending_logs),
                e
            )
            Toast.makeText(
                context,
                resourcesManager.getString(R.string.error_sending_logs),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun getLogFile(): File {
        return File(context.filesDir, LOG_FILE_NAME)
    }

    private fun logToFile(tag: String, message: String, level: String) {
        val timestamp = LocalDateTime.now()
        val logLine = String.format("%s %s/%s: %s\n", timestamp, level, tag, message)

        try {
            val logFile = getLogFile()

            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE_BYTES) {
                logFile.delete()
            }

            FileWriter(logFile, true).use { writer ->
                writer.append(logLine)
                writer.flush()
            }

        } catch (e: IOException) {
            Log.e(
                "FileLogger",
                resourcesManager.getString(R.string.error_adding_log_to_file),
                e
            )
        }
    }


    fun i(tag: String, message: String) {
        Log.i(tag, message)
        logToFile(tag, message, "I")
    }

    fun e(tag: String, message: String, tr: Throwable? = null) {
        val fullMessage = if (tr != null) {
            Log.e(tag, message, tr)
            "$message\n${Log.getStackTraceString(tr)}"
        } else {
            Log.e(tag, message)
            message
        }
        logToFile(tag, fullMessage, "E")
    }
}