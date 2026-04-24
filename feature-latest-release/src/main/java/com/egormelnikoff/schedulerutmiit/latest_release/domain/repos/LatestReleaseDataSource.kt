package com.egormelnikoff.schedulerutmiit.latest_release.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.dto.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.core.common.result.Result

interface LatestReleaseDataSource {
    suspend fun fetchLatestRelease(): Result<LatestReleaseFetchDto>
}