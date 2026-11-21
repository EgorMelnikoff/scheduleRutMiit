package com.egormelnikoff.schedulerutmiit.core.settings.data.remote

import com.egormelnikoff.schedulerutmiit.core.settings.data.remote.mapper.toDomain
import javax.inject.Inject

internal class LatestReleaseRemote @Inject constructor(
    private val githubApi: GithubApi
) {
    suspend fun fetchLatestRelease() =
        githubApi.getLatestRelease().toDomain()
}