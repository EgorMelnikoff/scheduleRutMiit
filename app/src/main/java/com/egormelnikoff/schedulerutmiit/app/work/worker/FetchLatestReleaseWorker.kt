package com.egormelnikoff.schedulerutmiit.app.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.domain.use_case.updates.FetchLatestReleaseUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FetchLatestReleaseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val fetchLatestReleaseUseCase: FetchLatestReleaseUseCase
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return when (fetchLatestReleaseUseCase()) {
            is com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result.Error -> Result.retry()
            is com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result.Success -> Result.success()
        }
    }
}