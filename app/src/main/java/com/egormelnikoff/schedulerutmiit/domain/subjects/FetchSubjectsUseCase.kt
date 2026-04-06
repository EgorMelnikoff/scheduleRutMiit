package com.egormelnikoff.schedulerutmiit.domain.subjects

import com.egormelnikoff.schedulerutmiit.app.network.model.SubjectModel
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
                    val subjectModels = mutableListOf<SubjectModel>()
                    subjectModels += subjectsListParser
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
                                    subjectModels += subjectsListParser
                                        .parseListSubjectsByPage(result.data)
                                        .toSubjects()
                                }
                            }
                        }
                    }
                    return@supervisorScope Result.Success(
                        subjectModels
                            .normalizeSubjects()
                            .sortedBy { s -> s.title }
                    )
                }
            }
        }
    }


    private fun MutableMap<String, MutableSet<String>>.toSubjects(): List<SubjectModel> {
        val result = mutableListOf<SubjectModel>()

        this.forEach { (subject, teachers) ->
            result.add(
                SubjectModel(
                    title = subject,
                    teachers = teachers
                )
            )
        }

        return result
    }

    private fun List<SubjectModel>.normalizeSubjects(): List<SubjectModel> {
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
            SubjectModel(
                title = title,
                teachers = teachers
            )
        }
    }
}