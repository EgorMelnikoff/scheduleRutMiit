package com.egormelnikoff.schedulerutmiit.search.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Person
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.dto.institutes.InstitutesDto

interface SearchRemoteDataSource {
    suspend fun fetchInstitutes(): Result<InstitutesDto>
    suspend fun fetchPeopleByQuery(query: String): Result<List<Person>>
}