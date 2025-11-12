package com.egormelnikoff.schedulerutmiit.app.work

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.app.work.worker.ScheduleWorker
import com.egormelnikoff.schedulerutmiit.app.work.worker.WidgetWorker
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.shared_prefs.SharedPreferencesManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface WorkScheduler {
    fun cancelPeriodicScheduleUpdating()
    fun cancelPeriodicWidgetUpdating()
    fun startPeriodicScheduleUpdating()
    fun startPeriodicWidgetUpdating()
}

class WorkSchedulerImpl @Inject constructor(
    private val workManager: WorkManager,
    private val sharedPreferencesManager: SharedPreferencesManager
): WorkScheduler {
    companion object {
        private const val UPDATING_SCHEDULE_PERIODICALLY = "updatingSchedulePeriodically"
        private const val UPDATING_SCHEDULE_INTERVAL: Long = 10
        private const val UPDATING_WIDGET_PERIODICALLY = "updatingWidgetPeriodically"
        private const val UPDATING_WIDGET_INTERVAL: Long = 15
    }

    override fun cancelPeriodicScheduleUpdating() {
        workManager.cancelUniqueWork(UPDATING_SCHEDULE_PERIODICALLY)
    }

    override fun cancelPeriodicWidgetUpdating() {
        workManager.cancelUniqueWork(UPDATING_WIDGET_PERIODICALLY)
    }

    override fun startPeriodicScheduleUpdating() {
        cancelPeriodicScheduleUpdating()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val scheduleWorkRequest = PeriodicWorkRequestBuilder<ScheduleWorker>(
            UPDATING_SCHEDULE_INTERVAL,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UPDATING_SCHEDULE_PERIODICALLY,
            ExistingPeriodicWorkPolicy.REPLACE,
            scheduleWorkRequest
        )
        sharedPreferencesManager.editBooleanPreference(
            name = "update_schedule_scheduled",
            value = true
        )
    }

    override fun startPeriodicWidgetUpdating() {
        cancelPeriodicWidgetUpdating()
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
        sharedPreferencesManager.editBooleanPreference(
            name = "update_widgets_scheduled",
            value = true
        )
    }
}