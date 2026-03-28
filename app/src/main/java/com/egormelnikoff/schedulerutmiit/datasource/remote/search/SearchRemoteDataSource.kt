package com.egormelnikoff.schedulerutmiit.datasource.remote.search

import com.egormelnikoff.schedulerutmiit.app.network.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.network.model.Person
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import org.jsoup.nodes.Document

interface SearchRemoteDataSource {
    suspend fun fetchInstitutes(): Result<Institutes>
    suspend fun fetchPeopleByQuery(query: String): Result<List<Person>>
    suspend fun fetchSubjects(id: String, page: Int): Result<Document>

}