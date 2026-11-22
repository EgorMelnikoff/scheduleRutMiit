package com.egormelnikoff.schedulerutmiit.search.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.v2.GroupDtoV2
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.v2.PeopleDtoV2
import retrofit2.http.GET
import retrofit2.http.Query

internal interface SearchApi {
    @GET(Endpoints.GROUPS_V2)
    suspend fun getGroups(): List<GroupDtoV2>

    @GET(Endpoints.PEOPLE_V2)
    suspend fun getPeople(
        @Query("name")
        name: String,
        @Query("profOnly")
        profOnly: Boolean = false,
        @Query("chunk")
        chunk: Int = 50,
        @Query("num")
        num: Int = 0,
        @Query("dept")
        dept: Int = 37
    ): PeopleDtoV2
}