package com.egormelnikoff.schedulerutmiit.datasource.remote.latest_release

import com.egormelnikoff.schedulerutmiit.app.network.NetworkHelper
import com.egormelnikoff.schedulerutmiit.datasource.remote.api.GithubApi
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
        callJsoup = null
    )
}