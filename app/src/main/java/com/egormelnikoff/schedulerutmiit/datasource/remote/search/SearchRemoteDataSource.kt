package com.egormelnikoff.schedulerutmiit.datasource.remote.search

import com.egormelnikoff.schedulerutmiit.app.dto.remote.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.person.PersonDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import org.jsoup.nodes.Document

interface SearchRemoteDataSource {
    suspend fun fetchInstitutes(): Result<InstitutesDto>
    suspend fun fetchPeopleByQuery(query: String): Result<List<PersonDto>>
    suspend fun fetchSubjects(id: String, page: Int): Result<Document>

}