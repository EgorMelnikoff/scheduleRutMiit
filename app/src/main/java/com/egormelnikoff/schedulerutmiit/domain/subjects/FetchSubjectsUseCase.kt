package com.egormelnikoff.schedulerutmiit.domain.subjects

import com.egormelnikoff.schedulerutmiit.app.dto.remote.subjects.SubjectDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.datasource.local.parser.SubjectsListParser
import com.egormelnikoff.schedulerutmiit.datasource.remote.search.SearchRemoteDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class FetchSubjectsUseCase @Inject constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val subjectsListParser: SubjectsListParser
) {
    suspend operator fun invoke(
        id: String
    ) = supervisorScope {
        searchRemoteDataSource.fetchSubjects(id, 1).let { firstPage ->
            when (firstPage) {
                is Result.Error -> {
                    return@supervisorScope firstPage
                }

                is Result.Success -> {
                    val subjectDtos = mutableListOf<SubjectDto>()
                    subjectDtos += subjectsListParser
                        .parseListSubjectsByPage(firstPage.data)
                        .toSubjects()

                    val pages = subjectsListParser.parsePagesCount(firstPage.data)
                    if (pages > 1) {
                        val deferredPages = (2..pages).map { currentPage ->
                            async {
                                searchRemoteDataSource.fetchSubjects(id, currentPage)
                            }
                        }

                        deferredPages.awaitAll().forEach { result ->
                            when (result) {
                                is Result.Error -> {
                                    return@supervisorScope result
                                }

                                is Result.Success -> {
                                    subjectDtos += subjectsListParser
                                        .parseListSubjectsByPage(result.data)
                                        .toSubjects()
                                }
                            }
                        }
                    }
                    return@supervisorScope Result.Success(
                        subjectDtos
                            .normalizeSubjects()
                            .sortedBy { s -> s.title }
                    )
                }
            }
        }
    }


    private fun MutableMap<String, MutableSet<String>>.toSubjects(): List<SubjectDto> {
        val result = mutableListOf<SubjectDto>()

        this.forEach { (subject, teachers) ->
            result.add(
                SubjectDto(
                    title = subject,
                    teachers = teachers
                )
            )
        }

        return result
    }

    private fun List<SubjectDto>.normalizeSubjects(): List<SubjectDto> {
        val map = mutableMapOf<String, MutableSet<String>>()

        forEach { subject ->
            subject.title.split(";").map {
                it.trim()
            }.forEach { splited ->
                map.getOrPut(splited) {
                    mutableSetOf()
                }.addAll(subject.teachers)
            }
        }

        return map.map { (title, teachers) ->
            SubjectDto(
                title = title,
                teachers = teachers
            )
        }
    }
}