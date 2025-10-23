package com.egormelnikoff.schedulerutmiit

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.data.datasource.local.database.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.shared_prefs.SharedPreferencesManager
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
    lateinit var namedScheduleDao: NamedScheduleDao

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var workScheduler: WorkScheduler


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        schedulePeriodicDataRefresh()
    }

    fun schedulePeriodicDataRefresh() {
        val isUpdateWidgetsWorkScheduled = sharedPreferencesManager.getBooleanPreference(
            name = "update_widgets_scheduled",
            initialValue = false
        )

        val isUpdateScheduleWorkScheduled = sharedPreferencesManager.getBooleanPreference(
            name = "update_schedule_scheduled",
            initialValue = false
        )

        applicationScope.launch {
            if (!isUpdateWidgetsWorkScheduled) {
                workScheduler.startPeriodicWidgetUpdating()
            }
            if (!isUpdateScheduleWorkScheduled) {
                val defaultNamedScheduleEntity = namedScheduleDao.getDefaultNamedScheduleEntity()
                defaultNamedScheduleEntity?.let {
                    workScheduler.startPeriodicScheduleUpdating()
                }
            }
        }
    }
}