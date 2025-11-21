package com.egormelnikoff.schedulerutmiit.app.work

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.app.work.worker.ScheduleWorker
import com.egormelnikoff.schedulerutmiit.app.work.worker.WidgetWorker
import com.egormelnikoff.schedulerutmiit.data.datasource.local.database.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.PreferencesDataStore
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface WorkScheduler {
    suspend fun startPeriodicWork()
    fun cancelPeriodicWork()
}

class WorkSchedulerImpl @Inject constructor(
    private val namedScheduleDao: NamedScheduleDao,
    private val workManager: WorkManager,
    private val preferencesDataStore: PreferencesDataStore
) : WorkScheduler {
    companion object {
        private const val UPDATING_SCHEDULE_PERIODICALLY = "updatingSchedulePeriodically"
        private const val UPDATING_SCHEDULE_INTERVAL: Long = 10 //Hours
        private const val UPDATING_WIDGET_PERIODICALLY = "updatingWidgetPeriodically"
        private const val UPDATING_WIDGET_INTERVAL: Long = 15 //Minutes
    }

    override suspend fun startPeriodicWork() {
        if (!isPeriodicWorkScheduled() && namedScheduleDao.getDefaultNamedScheduleEntity() != null) {
            cancelPeriodicWork()
            startPeriodicScheduleUpdating()
            startPeriodicWidgetUpdating()
        }
    }

    override fun cancelPeriodicWork() {
        cancelPeriodicScheduleUpdating()
        cancelPeriodicWidgetUpdating()
    }

    private suspend fun isPeriodicWorkScheduled() =
        preferencesDataStore.getScheduledScheduleWork() && preferencesDataStore.getScheduledWidgetWork()

    private suspend fun startPeriodicScheduleUpdating() {
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
        preferencesDataStore.setScheduledScheduleWork(true)
    }

    private fun cancelPeriodicScheduleUpdating() {
        workManager.cancelUniqueWork(UPDATING_SCHEDULE_PERIODICALLY)
    }

    private suspend fun startPeriodicWidgetUpdating() {
        val widgetWorkRequest = PeriodicWorkRequestBuilder<WidgetWorker>(
            UPDATING_WIDGET_INTERVAL,
            TimeUnit.MINUTES
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            UPDATING_WIDGET_PERIODICALLY,
            ExistingPeriodicWorkPolicy.KEEP,
            widgetWorkRequest
        )
        preferencesDataStore.setScheduledWidgetWork(true)
    }

    private fun cancelPeriodicWidgetUpdating() {
        workManager.cancelUniqueWork(UPDATING_WIDGET_PERIODICALLY)
    }
}