package com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import org.jsoup.nodes.Document

interface CurriculumRemoteDataSource {
    suspend fun fetchSubjects(id: String, page: Int): Result<Document>
}