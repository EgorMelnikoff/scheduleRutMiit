package com.egormelnikoff.schedulerutmiit.core.settings.data.local

import android.content.Context
import javax.inject.Inject

internal class AppInfoProvider @Inject constructor(
    private val context: Context
) {
    fun getVersionName(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}