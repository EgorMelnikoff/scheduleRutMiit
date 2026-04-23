package com.egormelnikoff.schedulerutmiit.data.repos

import com.egormelnikoff.schedulerutmiit.core.network.api.GithubApi
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkHelper
import com.egormelnikoff.schedulerutmiit.domain.repos.LatestReleaseDataSource
import javax.inject.Inject

class LatestReleaseDataSourceImpl @Inject constructor(
    private val githubApi: GithubApi,
    private val networkHelper: NetworkHelper
) : LatestReleaseDataSource {
    override suspend fun fetchLatestRelease() = networkHelper.callNetwork(
        requestType = "Latest Release",
        callApi = {
            githubApi.getLatestRelease()
        },
        timeoutMs = 2000,
        callJsoup = null
    )
}