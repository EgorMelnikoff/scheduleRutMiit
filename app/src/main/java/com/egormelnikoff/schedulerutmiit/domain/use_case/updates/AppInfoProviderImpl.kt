package com.egormelnikoff.schedulerutmiit.domain.use_case.updates

import android.content.Context
import javax.inject.Inject

class AppInfoProviderImpl @Inject constructor(
    private val context: Context
) : AppInfoProvider {

    override fun getVersionName(): String {
        return try {
            val packageInfo = context.packageManager
                .getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}