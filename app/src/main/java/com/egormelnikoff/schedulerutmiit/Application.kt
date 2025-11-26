package com.egormelnikoff.schedulerutmiit

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.egormelnikoff.schedulerutmiit.app.widget.receivers.EventsWidgetReceiver
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ScheduleApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workScheduler: WorkScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        managePeriodicWork()
    }

    private fun managePeriodicWork() {
        applicationScope.launch {
            manageWidgetUpdating()
            workScheduler.startPeriodicScheduleUpdating()
        }
    }


    private suspend fun manageWidgetUpdating() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(this, EventsWidgetReceiver::class.java)
        )
        if (widgetIds.isEmpty()) {
            workScheduler.cancelPeriodicWidgetUpdating()
        }
    }
}