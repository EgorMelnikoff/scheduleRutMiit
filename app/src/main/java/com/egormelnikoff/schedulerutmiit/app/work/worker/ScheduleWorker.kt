package com.egormelnikoff.schedulerutmiit.app.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val scheduleRepos: ScheduleRepos,
    private val logger: Logger
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val namedScheduleEntity = scheduleRepos.getDefaultNamedScheduleEntity()
        logger.i("ScheduleWorker", "Default schedule:\n$namedScheduleEntity")
        namedScheduleEntity ?: return Result.failure()

        return try {
            val result = scheduleRepos.updateSavedNamedSchedule(
                namedScheduleEntity = namedScheduleEntity,
                onStartUpdate = {
                    logger.i("ScheduleWorker", "Start schedule update")
                }
            )
            if (result is com.egormelnikoff.schedulerutmiit.data.Result.Success) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            logger.e("ScheduleWorker", "Failed to update schedule", e)
            Result.failure()
        }
    }
}