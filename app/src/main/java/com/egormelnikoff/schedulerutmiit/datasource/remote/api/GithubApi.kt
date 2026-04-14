package com.egormelnikoff.schedulerutmiit.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.app.dto.remote.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints
import retrofit2.Response
import retrofit2.http.GET

interface GithubApi {
    @GET(Endpoints.APP_GITHUB_API_LATEST_RELEASE)
    suspend fun getLatestRelease(): Response<LatestReleaseFetchDto>
}