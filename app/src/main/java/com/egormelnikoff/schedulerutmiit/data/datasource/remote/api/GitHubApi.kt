package com.egormelnikoff.schedulerutmiit.data.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.app.model.LatestRelease
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiHelper.Companion.LATEST_RELEASE
import retrofit2.Response
import retrofit2.http.GET

interface GitHubApi {
    @GET(LATEST_RELEASE)
    suspend fun latestRelease(): Response<LatestRelease>
}