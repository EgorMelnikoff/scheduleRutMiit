package com.egormelnikoff.schedulerutmiit.search.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.InstitutesDto
import retrofit2.http.GET

internal interface SearchApi {
    @GET(Endpoints.GROUPS)
    suspend fun getInstitutes(): InstitutesDto
}