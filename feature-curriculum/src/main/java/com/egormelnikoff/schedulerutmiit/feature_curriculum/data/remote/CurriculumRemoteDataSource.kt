package com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.fetcher.HtmlFetcher
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.mapper.toSubjects
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.model.SubjectDto
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.utils.SubjectsListParser
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.model.Subject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class CurriculumRemoteDataSource @Inject constructor(
    private val htmlFetcher: HtmlFetcher,
) {
    suspend fun fetchSubjects(id: String, page: Int): List<Subject> = coroutineScope {
        val firstPage = htmlFetcher(
            url = Endpoints.curriculumProfessorsUrl(id, page)
        )

        val subjects = mutableListOf<SubjectDto>()
        subjects += SubjectsListParser
            .parseListSubjectsByPage(firstPage)
            .toSubjects()

        val pages = SubjectsListParser.parsePagesCount(firstPage)
        if (pages > 1) {
            val deferredPages = (2..pages).map { currentPage ->
                async {
                    htmlFetcher(
                        url = Endpoints.curriculumProfessorsUrl(id, currentPage)
                    )
                }
            }

            deferredPages.awaitAll().forEach { result ->
                subjects += SubjectsListParser
                    .parseListSubjectsByPage(result)
                    .toSubjects()
            }
        }
        return@coroutineScope subjects
            .toDomain()
            .sortedBy { s -> s.title }
    }
}