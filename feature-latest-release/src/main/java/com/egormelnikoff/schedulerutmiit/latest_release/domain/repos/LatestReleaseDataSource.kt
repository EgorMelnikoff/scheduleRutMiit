package com.egormelnikoff.schedulerutmiit.latest_release.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.common.result.Result

interface LatestReleaseDataSource {
    suspend fun fetchLatestRelease(): Result<LatestRelease>
}