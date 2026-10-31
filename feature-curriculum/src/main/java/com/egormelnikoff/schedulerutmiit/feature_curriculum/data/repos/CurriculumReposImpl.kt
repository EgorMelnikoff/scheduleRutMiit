package com.egormelnikoff.schedulerutmiit.feature_curriculum.data.repos

import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.CurriculumRemoteDataSource
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.mapper.toSubjects
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.model.SubjectDto
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.utils.SubjectsListParser
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.model.Subject
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos.CurriculumRepos
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

internal class CurriculumReposImpl @Inject constructor(
    private val curriculumRemoteDataSource: CurriculumRemoteDataSource
) : CurriculumRepos {
    override suspend fun fetchSubjects(id: String, page: Int): List<Subject> = supervisorScope {
        curriculumRemoteDataSource.fetchSubjects(id, 1).let { firstPage ->
            val subjectDtos = mutableListOf<SubjectDto>()
            subjectDtos += SubjectsListParser
                .parseListSubjectsByPage(firstPage)
                .toSubjects()

            val pages = SubjectsListParser.parsePagesCount(firstPage)
            if (pages > 1) {
                val deferredPages = (2..pages).map { currentPage ->
                    async {
                        curriculumRemoteDataSource.fetchSubjects(id, currentPage)
                    }
                }

                deferredPages.awaitAll().forEach { result ->
                    subjectDtos += SubjectsListParser
                        .parseListSubjectsByPage(result)
                        .toSubjects()
                }
            }
            return@supervisorScope subjectDtos
                .toDomain()
                .sortedBy { s -> s.title }
        }
    }

}