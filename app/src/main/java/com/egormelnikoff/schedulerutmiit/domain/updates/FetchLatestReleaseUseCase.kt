package com.egormelnikoff.schedulerutmiit.domain.updates

import com.egormelnikoff.schedulerutmiit.app.dto.remote.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.app.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.datasource.remote.latest_release.LatestReleaseDataSource
import javax.inject.Inject

class FetchLatestReleaseUseCase @Inject constructor(
    private val latestReleaseDataSource: LatestReleaseDataSource,
    private val preferencesDataStore: PreferencesDataStore
) {
    suspend operator fun invoke(): Result<LatestReleaseFetchDto> {
        val result = latestReleaseDataSource.fetchLatestRelease()
        if (result is Result.Success) {
            preferencesDataStore.setLatestRelease(result.data)
        }
        return result
    }
}