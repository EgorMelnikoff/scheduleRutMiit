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
        val namedSchedule= namedScheduleRepos.getDefault()
        logger.i("ScheduleWorker", "Default schedule:\n$namedSchedule")
        namedSchedule ?: return Result.success()

        val updateResult = refreshNamedScheduleUseCase.update(
            namedSchedule = namedSchedule,
            onStartUpdate = {
                logger.i("ScheduleWorker", "Start schedule update")
            },
            deletableOldSchedules = preferencesDataSource.schedulesDeletableFlow.first()
        )

        return when(updateResult) {
            is CustomResult.Success -> {
                Result.success()
            }

            is CustomResult.Error -> {
                Result.retry()
            }
        }
    }
}