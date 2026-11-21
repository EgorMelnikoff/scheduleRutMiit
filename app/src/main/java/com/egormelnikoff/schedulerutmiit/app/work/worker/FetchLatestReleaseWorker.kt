package com.egormelnikoff.schedulerutmiit.app.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.egormelnikoff.schedulerutmiit.core.settings.data.work.worker.FetchLatestReleaseWorker as ActualFetchLatestReleaseWorker

// Старые установки хранят это имя класса в БД WorkManager — не удалять
@HiltWorker
class FetchLatestReleaseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    settingsRepos: SettingsRepos
) : ActualFetchLatestReleaseWorker(appContext, workerParams, settingsRepos)
