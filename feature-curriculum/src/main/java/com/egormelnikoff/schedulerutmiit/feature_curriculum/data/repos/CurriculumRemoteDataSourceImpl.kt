package com.egormelnikoff.schedulerutmiit.feature_curriculum.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkExecutor
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos.CurriculumRemoteDataSource
import org.jsoup.nodes.Document
import javax.inject.Inject

class CurriculumRemoteDataSourceImpl @Inject constructor(
    private val networkExecutor: NetworkExecutor
) : CurriculumRemoteDataSource {

    override suspend fun fetchSubjects(id: String, page: Int): Result<Document> {
        return networkExecutor.callHtml(
            url = Endpoints.curriculumProfessorsUrl(id, page)
        )
    }
}