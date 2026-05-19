package com.egormelnikoff.schedulerutmiit.app.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataSource
import com.egormelnikoff.schedulerutmiit.core.network.logger.Logger
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.RefreshNamedScheduleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import com.egormelnikoff.schedulerutmiit.core.common.result.Result as CustomResult

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val namedScheduleRepos: NamedScheduleRepos,
    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    private val logger: Logger,
    private val preferencesDataSource: PreferencesDataSource
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val namedSchedules = namedScheduleRepos.getAll()
        logger.i("ScheduleWorker", "Schedules size: ${namedSchedules.size}")
        if (namedSchedules.isEmpty()) {
            return Result.success()
        }

        val results = mutableListOf<CustomResult<String>>()
        for (namedSchedule in namedSchedules) {
            if (results.size == 3) break
            val updateResult = refreshNamedScheduleUseCase.update(
                namedSchedule = namedSchedule,
                onStartUpdate = {
                    logger.i("ScheduleWorker", "Update ${namedSchedule.apiId}")
                },
                deletableOldSchedules = preferencesDataSource.schedulesDeletableFlow.first()
            )
            results.add(updateResult)
        }

        return when {
            results.all { it is CustomResult.Success } -> {
                Result.success()
            }

            else -> {
                Result.retry()
            }
        }
    }
}