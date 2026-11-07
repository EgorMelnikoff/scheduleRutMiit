package com.egormelnikoff.schedulerutmiit

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.egormelnikoff.schedulerutmiit.core.settings.data.work.LatestReleaseWorkScheduler
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.work.ScheduleWorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ScheduleApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var latestReleaseWorkScheduler: LatestReleaseWorkScheduler

    @Inject
    lateinit var scheduleWorkScheduler: ScheduleWorkScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        latestReleaseWorkScheduler.startPeriodicFetchingLatestVersion()
        scheduleWorkScheduler.startPeriodicScheduleUpdating()
    }
}