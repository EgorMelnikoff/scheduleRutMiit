package com.egormelnikoff.schedulerutmiit.latest_release.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.dto.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.LatestReleaseDataSource
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