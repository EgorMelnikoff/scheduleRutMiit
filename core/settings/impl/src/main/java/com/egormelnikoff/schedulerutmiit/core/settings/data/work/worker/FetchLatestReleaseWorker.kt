package com.egormelnikoff.schedulerutmiit.core.settings.data.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
internal class FetchLatestReleaseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingsRepos: SettingsRepos,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            settingsRepos.updateLatestRelease()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}