package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.data.remote.dto.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result

interface LatestReleaseDataSource {
    suspend fun fetchLatestRelease(): Result<LatestReleaseFetchDto>
}