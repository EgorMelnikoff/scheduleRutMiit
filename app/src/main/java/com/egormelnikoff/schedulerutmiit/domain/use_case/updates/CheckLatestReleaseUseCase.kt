package com.egormelnikoff.schedulerutmiit.domain.use_case.updates

import com.egormelnikoff.schedulerutmiit.data.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CheckLatestReleaseUseCase @Inject constructor(
    private val fetchLatestReleaseUseCase: FetchLatestReleaseUseCase,
    private val preferencesDataStore: PreferencesDataStore,
    private val appInfoProvider: AppInfoProvider
) {
    companion object {
        val UPDATE_THRESHOLD_MS = TimeUnit.MINUTES.toMillis(5)
    }

    suspend operator fun invoke(
        fetchForce: Boolean = false
    ): Boolean {
        val cached = preferencesDataStore.latestReleaseFlow.first()

        val latest = if (fetchForce && cached != null && cached.isOutdated(UPDATE_THRESHOLD_MS)) {
            when (val result = fetchLatestReleaseUseCase()) {
                is Result.Success -> result.data
                else -> cached
            }
        } else cached


        return latest?.let {
            it.tag.parseVersionCode() > appInfoProvider.getVersionName().parseVersionCode()
        } ?: false
    }

    private fun String.parseVersionCode(): Int {
        val clean = this.removePrefix("v")
        val parts = clean.split(".")

        val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

        return major * 10000 + minor * 100 + patch
    }
}