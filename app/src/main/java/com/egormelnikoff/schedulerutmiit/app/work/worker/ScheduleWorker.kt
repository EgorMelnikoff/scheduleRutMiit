package com.egormelnikoff.schedulerutmiit.app.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import com.egormelnikoff.schedulerutmiit.data.Result as ApiResult

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val scheduleRepos: ScheduleRepos,
    private val logger: Logger,
    private val preferencesDataStore: PreferencesDataStore
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val namedScheduleEntity = scheduleRepos.getDefaultNamedScheduleEntity()
        logger.i("ScheduleWorker", "Default schedule:\n$namedScheduleEntity")
        namedScheduleEntity ?: return Result.failure()

        val updateResult = scheduleRepos.updateSavedNamedSchedule(
            namedScheduleEntity = namedScheduleEntity,
            onStartUpdate = {
                logger.i("ScheduleWorker", "Start schedule update")
            },
            deletableOldSchedules = preferencesDataStore.schedulesDeletableFlow.first()
        )

        return when(updateResult) {
            is ApiResult.Success -> {
                Result.success()
            }

            is ApiResult.Error -> {
                Result.retry()
            }
        }
    }
}