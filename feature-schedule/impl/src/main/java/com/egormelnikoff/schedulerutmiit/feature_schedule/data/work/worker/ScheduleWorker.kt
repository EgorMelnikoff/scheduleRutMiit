package com.egormelnikoff.schedulerutmiit.feature_schedule.data.work.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.RefreshNamedScheduleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val namedScheduleRepos: NamedScheduleRepos,
    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    private val settingsRepos: SettingsRepos
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
                deletableOldSchedules = settingsRepos.getSchedulesDeletable()
            )
            results.add(updateResult)
        }

        return when {
            results.all { it } -> Result.success()
            else -> Result.retry()
        }
    }
}