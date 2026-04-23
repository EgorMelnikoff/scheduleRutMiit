package com.egormelnikoff.schedulerutmiit.core.network.logger

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import javax.inject.Inject

class Logger @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val LOG_FILE_NAME = "app_logs.txt"
        private const val MAX_LOG_SIZE_BYTES = 1024 * 1024L
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
                "Error adding log to file",
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