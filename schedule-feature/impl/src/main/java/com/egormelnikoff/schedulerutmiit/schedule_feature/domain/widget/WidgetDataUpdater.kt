package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.widget

import androidx.work.ListenableWorker

interface WidgetDataUpdater {
    suspend fun updateAll(): ListenableWorker.Result
}