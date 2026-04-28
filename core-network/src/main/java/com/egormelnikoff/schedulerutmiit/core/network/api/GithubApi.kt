package com.egormelnikoff.schedulerutmiit.core.network.api

import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints
import retrofit2.Response
import retrofit2.http.GET

interface GithubApi {
    @GET(Endpoints.APP_GITHUB_API_LATEST_RELEASE)
    suspend fun getLatestRelease(): Response<LatestRelease>
}