package com.egormelnikoff.schedulerutmiit.app.work

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.app.work.worker.ScheduleWorker
import com.egormelnikoff.schedulerutmiit.app.work.worker.WidgetWorker
import com.egormelnikoff.schedulerutmiit.data.datasource.local.database.NamedScheduleDao
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface WorkScheduler {
    suspend fun isScheduledScheduleWork(): Boolean
    suspend fun haveDefaultSchedule(): Boolean
    suspend fun startPeriodicScheduleUpdating()
    suspend fun cancelPeriodicScheduleUpdating()

    suspend fun startPeriodicWidgetUpdating()
    suspend fun cancelPeriodicWidgetUpdating()
}

class WorkSchedulerImpl @Inject constructor(
    private val namedScheduleDao: NamedScheduleDao,
    private val workManager: WorkManager
) : WorkScheduler {
    companion object {
        private const val UPDATING_SCHEDULE_PERIODICALLY = "updatingSchedulePeriodically"
        private const val UPDATING_SCHEDULE_INTERVAL = 10L //Hours
        private const val UPDATING_WIDGET_PERIODICALLY = "updatingWidgetPeriodically"
        private const val UPDATING_WIDGET_INTERVAL = 15L //Minutes
    }

    override suspend fun isScheduledScheduleWork(): Boolean {
        val workInfos = workManager.getWorkInfosByTag(UPDATING_SCHEDULE_PERIODICALLY).get()
        val workInfo = workInfos.firstOrNull()

        return workInfo != null && (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING)
    }

    override suspend fun haveDefaultSchedule(): Boolean {
        return namedScheduleDao.getDefaultNamedScheduleEntity() != null
    }


    override suspend fun startPeriodicScheduleUpdating() {
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
            ExistingPeriodicWorkPolicy.REPLACE,
            scheduleWorkRequest
        )
    }

    override suspend fun cancelPeriodicScheduleUpdating() {
        workManager.cancelUniqueWork(UPDATING_SCHEDULE_PERIODICALLY)
    }

    override suspend fun startPeriodicWidgetUpdating() {
        val widgetWorkRequest = PeriodicWorkRequestBuilder<WidgetWorker>(
            UPDATING_WIDGET_INTERVAL,
            TimeUnit.MINUTES
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            UPDATING_WIDGET_PERIODICALLY,
            ExistingPeriodicWorkPolicy.REPLACE,
            widgetWorkRequest
        )
    }

    override suspend fun cancelPeriodicWidgetUpdating() {
        workManager.cancelUniqueWork(UPDATING_WIDGET_PERIODICALLY)
    }
}