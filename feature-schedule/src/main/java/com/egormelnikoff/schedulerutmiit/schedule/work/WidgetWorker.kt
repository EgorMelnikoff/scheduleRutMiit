package com.egormelnikoff.schedulerutmiit.schedule.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.schedule.widget.WidgetDataUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WidgetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val widgetDataUpdater: WidgetDataUpdater
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
       return widgetDataUpdater.updateAll()
    }
}