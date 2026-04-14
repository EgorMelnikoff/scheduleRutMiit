package com.egormelnikoff.schedulerutmiit.app.work

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.app.work.worker.FetchLatestReleaseWorker
import com.egormelnikoff.schedulerutmiit.app.work.worker.ScheduleWorker
import com.egormelnikoff.schedulerutmiit.app.work.worker.WidgetWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val FETCH_LATEST_RELEASE_PERIODICALLY = "fetchLatestReleasePeriodically"
        private const val FETCH_LATEST_INTERVAL = 10L
        private const val UPDATING_SCHEDULE_PERIODICALLY = "updatingSchedulePeriodically"
        private const val UPDATING_SCHEDULE_INTERVAL = 24L //Hours
        private const val UPDATING_WIDGET_PERIODICALLY = "updatingWidgetPeriodically"
        private const val UPDATING_WIDGET_INTERVAL = 15L //Minutes
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

    fun startPeriodicScheduleUpdating() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val scheduleWorkRequest = PeriodicWorkRequestBuilder<ScheduleWorker>(
            UPDATING_SCHEDULE_INTERVAL,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(UPDATING_SCHEDULE_PERIODICALLY)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UPDATING_SCHEDULE_PERIODICALLY,
            ExistingPeriodicWorkPolicy.KEEP,
            scheduleWorkRequest
        )
    }

    fun cancelPeriodicScheduleUpdating() {
        workManager.cancelUniqueWork(UPDATING_SCHEDULE_PERIODICALLY)
    }

    fun startPeriodicWidgetUpdating() {
        val widgetWorkRequest = PeriodicWorkRequestBuilder<WidgetWorker>(
            UPDATING_WIDGET_INTERVAL,
            TimeUnit.MINUTES
        )
            .addTag(UPDATING_WIDGET_PERIODICALLY)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UPDATING_WIDGET_PERIODICALLY,
            ExistingPeriodicWorkPolicy.KEEP,
            widgetWorkRequest
        )
    }

    fun cancelPeriodicWidgetUpdating() {
        workManager.cancelUniqueWork(UPDATING_WIDGET_PERIODICALLY)
    }
}