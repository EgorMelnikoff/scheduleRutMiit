package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos

import androidx.work.ListenableWorker

interface WidgetDataUpdater {
    suspend fun updateAll(): ListenableWorker.Result
}