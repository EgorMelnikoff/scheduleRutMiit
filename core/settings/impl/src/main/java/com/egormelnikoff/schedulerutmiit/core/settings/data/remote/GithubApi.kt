package com.egormelnikoff.schedulerutmiit.core.settings.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.settings.data.remote.dto.LatestReleaseDto
import retrofit2.http.GET

internal interface GithubApi {
    @GET(Endpoints.APP_GITHUB_API_LATEST_RELEASE)
    suspend fun getLatestRelease(): LatestReleaseDto
}