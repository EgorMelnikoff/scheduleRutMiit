package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.dto.latest_release.LatestReleaseFetchDto

interface LatestReleaseDataSource {
    suspend fun fetchLatestRelease(): Result<LatestReleaseFetchDto>
}