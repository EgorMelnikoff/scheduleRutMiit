package com.egormelnikoff.schedulerutmiit.data.repos.search.remote

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.model.Institute
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.Subject
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.NetworkHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.local.parser.SearchParser
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import org.jsoup.Jsoup
import javax.inject.Inject

class SearchRemoteReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val networkHelper: NetworkHelper,
    private val searchParser: SearchParser
) : SearchRemoteRepos {
    override suspend fun getPeopleByQuery(query: String): Result<List<Person>> {
        networkHelper.callNetwork(
            requestType = "Person",
            requestParams = "Query: $query",
            callJsoup = {
                Jsoup.connect(Endpoints.peopleUrl(query)).get()
            },
            callApi = null
        ).let {
            return when (it) {
                is Result.Error -> it

                is Result.Success -> {
                    Result.Success(searchParser.parsePeople(it.data))
                }
            }
        }
    }

    override suspend fun getSubjectsByCurriculum(id: String) = supervisorScope {
        networkHelper.callNetwork(
            requestType = "Subjects",
            requestParams = "Id: $id; Page: 1",
            callJsoup = {
                Jsoup.connect(Endpoints.curriculumProfessorsUrl(id, 1)).get()
            },
            callApi = null
        ).let {
            when (it) {
                is Result.Error -> {
                    return@supervisorScope it
                }

                is Result.Success -> {
                    val subjects = mutableListOf<Subject>()
                    subjects += searchParser
                        .parseListSubjectsByPage(it.data)
                        .toSubjects()

                    val pages = searchParser.parsePagesCount(it.data)
                    if (pages > 1) {
                        val deferredPages = (2..pages).map { currentPage ->
                            async {
                                networkHelper.callNetwork(
                                    requestType = "Subjects",
                                    requestParams = "Id: $id; Page: $currentPage",
                                    callJsoup = {
                                        Jsoup.connect(
                                            Endpoints.curriculumProfessorsUrl(id, currentPage)
                                        ).get()
                                    },
                                    callApi = null
                                )
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

    override suspend fun getGroupsByQuery(
        institutes: Institutes,
        query: String
    ): Result<List<Group>> {
        val groups = getGroups(institutes.institutes)
        val filteredGroups = groups.filter {
            compareValues(it.name, query)
        }
        return Result.Success(filteredGroups)
    }

    override suspend fun fetchInstitutes(): Result<Institutes> = networkHelper.callNetwork(
        requestType = "Institutes",
        callApi = {
            miitApi.getInstitutes()
        },
        callJsoup = null
    )

    private fun compareValues(comparableValue: String, query: String): Boolean {
        val cleanValue = comparableValue.filter { !it.isWhitespace() }
        val cleanQuery = query.filter { !it.isWhitespace() }

        return cleanValue.contains(cleanQuery, ignoreCase = true)
    }

    private fun getGroups(institutes: List<Institute>): List<Group> {
        return institutes.flatMap { institute ->
            institute.courses.flatMap { course ->
                course.specialties.flatMap { specialty ->
                    specialty.groups
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