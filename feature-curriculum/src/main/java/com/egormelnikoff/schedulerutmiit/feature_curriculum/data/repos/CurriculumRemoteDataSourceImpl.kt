package com.egormelnikoff.schedulerutmiit.feature_curriculum.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkHelper
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos.CurriculumRemoteDataSource
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class CurriculumRemoteDataSourceImpl @Inject constructor(
    private val networkHelper: NetworkHelper
) : CurriculumRemoteDataSource {

    override suspend fun fetchSubjects(id: String, page: Int): Result<Document> {
        return networkHelper.callNetwork(
            requestType = "Subjects",
            requestParams = "Id: $id; Page: $page",
            callJsoup = {
                Jsoup.connect(Endpoints.curriculumProfessorsUrl(id, page)).get()
            },
            callApi = null
        )
    }
}