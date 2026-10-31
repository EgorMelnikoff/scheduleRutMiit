package com.egormelnikoff.schedulerutmiit.latest_release.data.work

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.latest_release.data.work.worker.FetchLatestReleaseWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LatestReleaseWorkScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val FETCH_LATEST_RELEASE_PERIODICALLY = "fetchLatestReleasePeriodically"
        private const val FETCH_LATEST_INTERVAL = 10L //Hours
    }

    fun startPeriodicFetchingLatestVersion() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<FetchLatestReleaseWorker>(
            FETCH_LATEST_INTERVAL,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(FETCH_LATEST_RELEASE_PERIODICALLY)
            .build()

        workManager.enqueueUniquePeriodicWork(
            FETCH_LATEST_RELEASE_PERIODICALLY,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}