package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos

import androidx.work.ListenableWorker

interface WidgetDataUpdater {
    suspend fun updateAll(): ListenableWorker.Result
}