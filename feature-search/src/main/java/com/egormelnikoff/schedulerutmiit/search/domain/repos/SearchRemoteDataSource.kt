package com.egormelnikoff.schedulerutmiit.search.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.person.PersonDto

interface SearchRemoteDataSource {
    suspend fun fetchInstitutes(): Result<InstitutesDto>
    suspend fun fetchPeopleByQuery(query: String): Result<List<PersonDto>>
}