package com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.fetcher.HtmlFetcher
import org.jsoup.nodes.Document
import javax.inject.Inject

class CurriculumRemoteDataSource @Inject constructor(
    private val htmlFetcher: HtmlFetcher,
) {
    suspend fun fetchSubjects(id: String, page: Int): Document {
        return htmlFetcher(
            url = Endpoints.curriculumProfessorsUrl(id, page)
        )
    }
}