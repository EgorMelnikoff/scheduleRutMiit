package com.egormelnikoff.schedulerutmiit.latest_release.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.api.GithubApi
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkExecutor
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.LatestReleaseDataSource
import javax.inject.Inject

class LatestReleaseDataSourceImpl @Inject constructor(
    private val githubApi: GithubApi,
    private val networkExecutor: NetworkExecutor
) : LatestReleaseDataSource {
    override suspend fun fetchLatestRelease(): Result<LatestRelease> = networkExecutor.callApi {
        githubApi.getLatestRelease()
    }
}