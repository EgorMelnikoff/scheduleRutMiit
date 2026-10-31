package com.egormelnikoff.schedulerutmiit.latest_release.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.latest_release.data.remote.dto.LatestReleaseDto
import retrofit2.http.GET

interface GithubApi {
    @GET(Endpoints.APP_GITHUB_API_LATEST_RELEASE)
    suspend fun getLatestRelease(): LatestReleaseDto
}