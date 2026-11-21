package com.egormelnikoff.schedulerutmiit.app.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.work.worker.WidgetWorker as ActualWidgetWorker
import com.egormelnikoff.schedulerutmiit.feature_schedule.glance.model.WidgetDataUpdaterImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// Старые установки хранят это имя класса в БД WorkManager — не удалять
@HiltWorker
class WidgetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    widgetDataUpdater: WidgetDataUpdaterImpl
) : ActualWidgetWorker(appContext, workerParams, widgetDataUpdater)
