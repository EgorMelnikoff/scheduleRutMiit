package com.egormelnikoff.schedulerutmiit.latest_release.data.remote

import com.egormelnikoff.schedulerutmiit.latest_release.data.remote.mapper.toDomain
import javax.inject.Inject

class LatestReleaseRemote @Inject constructor(
    private val githubApi: GithubApi
) {
    suspend fun fetchLatestRelease() =
        githubApi.getLatestRelease().toDomain()
}