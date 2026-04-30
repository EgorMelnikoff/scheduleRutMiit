package com.egormelnikoff.schedulerutmiit.schedule.domain.widget

import androidx.work.ListenableWorker

interface WidgetDataUpdater {
    suspend fun updateAll(): ListenableWorker.Result
}