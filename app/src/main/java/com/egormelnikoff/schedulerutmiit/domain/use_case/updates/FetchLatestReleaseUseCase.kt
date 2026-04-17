package com.egormelnikoff.schedulerutmiit.domain.use_case.updates

import com.egormelnikoff.schedulerutmiit.data.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.remote.dto.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result
import com.egormelnikoff.schedulerutmiit.domain.repos.LatestReleaseDataSource
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