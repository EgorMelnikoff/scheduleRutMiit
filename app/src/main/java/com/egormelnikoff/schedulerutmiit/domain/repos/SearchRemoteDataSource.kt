package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.data.remote.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.person.PersonDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result
import org.jsoup.nodes.Document

interface SearchRemoteDataSource {
    suspend fun fetchInstitutes(): Result<InstitutesDto>
    suspend fun fetchPeopleByQuery(query: String): Result<List<PersonDto>>
    suspend fun fetchSubjects(id: String, page: Int): Result<Document>

}