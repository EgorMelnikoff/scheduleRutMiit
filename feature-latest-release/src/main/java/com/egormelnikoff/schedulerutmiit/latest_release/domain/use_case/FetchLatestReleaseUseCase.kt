package com.egormelnikoff.schedulerutmiit.latest_release.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataSource
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.LatestReleaseDataSource
import javax.inject.Inject

class FetchLatestReleaseUseCase @Inject constructor(
    private val latestReleaseDataSource: LatestReleaseDataSource,
    private val preferencesDataSource: PreferencesDataSource
) {
    suspend operator fun invoke(): Result<LatestRelease> {
        return when (val result = latestReleaseDataSource.fetchLatestRelease()) {
            is Result.Error -> Result.Error(result.typedError)
            is Result.Success -> {
                preferencesDataSource.setLatestRelease(result.data)
                result
            }
        }
    }
}