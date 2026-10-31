package com.egormelnikoff.schedulerutmiit.latest_release.data.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.LatestReleaseRepos
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FetchLatestReleaseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val latestReleaseRepos: LatestReleaseRepos,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            latestReleaseRepos.updateLatestRelease()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}