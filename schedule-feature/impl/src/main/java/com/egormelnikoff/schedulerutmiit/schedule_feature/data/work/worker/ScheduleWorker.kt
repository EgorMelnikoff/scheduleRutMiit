package com.egormelnikoff.schedulerutmiit.schedule_feature.data.work.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.core.common.domain.repos.PreferencesRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.RefreshNamedScheduleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val namedScheduleRepos: NamedScheduleRepos,
    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    private val preferencesRepos: PreferencesRepos
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val namedSchedules = namedScheduleRepos.getAll()
        Log.i("ScheduleWorker", "Schedules size: ${namedSchedules.size}")
        if (namedSchedules.isEmpty()) {
            return Result.success()
        }

        val results = mutableListOf<Boolean>()
        for (namedSchedule in namedSchedules) {
            if (results.size == 3) break
            val updateResult = refreshNamedScheduleUseCase.update(
                namedSchedule = namedSchedule,
                onStartUpdate = {
                    Log.i("ScheduleWorker", "Update ${namedSchedule.apiId}")
                },
                deletableOldSchedules = preferencesRepos.schedulesDeletableFlow.first()
            )
            results.add(updateResult)
        }

        return when {
            results.all { it } -> Result.success()
            else -> Result.retry()
        }
    }
}