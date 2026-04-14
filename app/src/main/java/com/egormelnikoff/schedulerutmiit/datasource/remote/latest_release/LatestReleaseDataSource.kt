package com.egormelnikoff.schedulerutmiit.datasource.remote.latest_release

import com.egormelnikoff.schedulerutmiit.app.dto.remote.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result

interface LatestReleaseDataSource {
    suspend fun fetchLatestRelease(): Result<LatestReleaseFetchDto>
}