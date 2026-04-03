package com.egormelnikoff.schedulerutmiit.datasource.remote.search

import com.egormelnikoff.schedulerutmiit.app.network.model.InstitutesModel
import com.egormelnikoff.schedulerutmiit.app.network.model.PersonModel
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import org.jsoup.nodes.Document

interface SearchRemoteDataSource {
    suspend fun fetchInstitutes(): Result<InstitutesModel>
    suspend fun fetchPeopleByQuery(query: String): Result<List<PersonModel>>
    suspend fun fetchSubjects(id: String, page: Int): Result<Document>

}