package com.egormelnikoff.schedulerutmiit.search.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.person.PersonDto
import org.jsoup.nodes.Document

interface SearchRemoteDataSource {
    suspend fun fetchInstitutes(): Result<InstitutesDto>
    suspend fun fetchPeopleByQuery(query: String): Result<List<PersonDto>>
}