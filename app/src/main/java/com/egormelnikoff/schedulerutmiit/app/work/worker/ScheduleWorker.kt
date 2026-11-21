package com.egormelnikoff.schedulerutmiit.app.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.work.worker.ScheduleWorker as ActualScheduleWorker
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.RefreshNamedScheduleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// Старые установки хранят это имя класса в БД WorkManager — не удалять
@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    namedScheduleRepos: NamedScheduleRepos,
    refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    settingsRepos: SettingsRepos
) : ActualScheduleWorker(
    appContext,
    workerParams,
    namedScheduleRepos,
    refreshNamedScheduleUseCase,
    settingsRepos
)
