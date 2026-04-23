package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.person.PersonDto
import org.jsoup.nodes.Document

interface SearchRemoteDataSource {
    suspend fun fetchInstitutes(): Result<InstitutesDto>
    suspend fun fetchPeopleByQuery(query: String): Result<List<PersonDto>>
    suspend fun fetchSubjects(id: String, page: Int): Result<Document>

}