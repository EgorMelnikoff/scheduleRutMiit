package com.egormelnikoff.schedulerutmiit.domain.subjects

import com.egormelnikoff.schedulerutmiit.app.network.model.Subject
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.datasource.local.parser.SearchParser
import com.egormelnikoff.schedulerutmiit.datasource.remote.search.SearchRemoteDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class FetchSubjectsUseCase @Inject constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val searchParser: SearchParser
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
                    val subjects = mutableListOf<Subject>()
                    subjects += searchParser
                        .parseListSubjectsByPage(firstPage.data)
                        .toSubjects()

                    val pages = searchParser.parsePagesCount(firstPage.data)
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
                                    subjects += searchParser
                                        .parseListSubjectsByPage(result.data)
                                        .toSubjects()
                                }
                            }
                        }
                    }
                    return@supervisorScope Result.Success(
                        subjects
                            .normalizeSubjects()
                            .sortedBy { s -> s.title }
                    )
                }
            }
        }
    }


    private fun MutableMap<String, MutableSet<String>>.toSubjects(): List<Subject> {
        val result = mutableListOf<Subject>()

        this.forEach { (subject, teachers) ->
            result.add(
                Subject(
                    title = subject,
                    teachers = teachers
                )
            )
        }

        return result
    }

    private fun List<Subject>.normalizeSubjects(): List<Subject> {
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
            Subject(
                title = title,
                teachers = teachers
            )
        }
    }
}