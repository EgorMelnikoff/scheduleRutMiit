package com.egormelnikoff.schedulerutmiit.data.repos.search.impl

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.model.Institute
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.Subject
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.Dao
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.NetworkHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import org.jsoup.Jsoup
import javax.inject.Inject

class SearchReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val networkHelper: NetworkHelper,
    private val parser: Parser,
    private val dao: Dao
) : SearchRepos {
    override suspend fun getPeopleByQuery(query: String): Result<List<Person>> {
        val document = networkHelper.callNetwork(
            requestType = "Person",
            requestParams = "Query: $query",
            callParser = {
                Jsoup.connect(Endpoints.peopleUrl(query)).get()
            },
            callApi = null
        )

        return when (document) {
            is Result.Success -> {
                Result.Success(parser.parsePeople(document.data))
            }

            is Result.Error -> {
                Result.Error(document.typedError)
            }
        }
    }

    override suspend fun getSubjectsByCurriculum(id: String): Result<List<Subject>> =
        supervisorScope {
            val document = networkHelper.callNetwork(
                requestType = "Subjects",
                requestParams = "Id: $id; Page: 1",
                callParser = {
                    Jsoup.connect(Endpoints.curriculumProfessorsUrl(id, 1)).get()
                },
                callApi = null
            )

            when (document) {
                is Result.Error -> {
                    return@supervisorScope document
                }

                is Result.Success -> {
                    val result = mutableListOf<Subject>()
                    result += parser.parseListSubjectsByPage(document.data).toSubjects()

                    val pages = parser.parsePagesCount(document.data)
                    if (pages > 1) {
                        val deferredPages = (2..pages).map { currentPage ->
                            async {
                                networkHelper.callNetwork(
                                    requestType = "Subjects",
                                    requestParams = "Id: $id; Page: $currentPage",
                                    callParser = {
                                        Jsoup.connect(
                                            Endpoints.curriculumProfessorsUrl(id, currentPage)
                                        ).get()
                                    },
                                    callApi = null
                                )
                            }
                        }

                        val results = deferredPages.awaitAll()

                        results.forEach { item ->
                            when (item) {
                                is Result.Error -> {
                                    return@supervisorScope item
                                }

                                is Result.Success -> {
                                    result += parser.parseListSubjectsByPage(item.data).toSubjects()
                                }
                            }
                        }
                    }
                    return@supervisorScope Result.Success(
                        result
                            .normalizeSubjects()
                            .sortedBy { it.title }
                    )
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

    override suspend fun fetchInstitutes(): Result<Institutes> =
        networkHelper.callNetwork(
            requestType = "Institutes",
            callApi = {
                miitApi.getInstitutes()
            },
            callParser = null
        )

    override suspend fun saveSearchQuery(searchQuery: SearchQuery) {
        val savedQuery = dao.getSearchQueryByApiId(searchQuery.apiId)
        savedQuery?.let {
            dao.deleteSearchQuery(savedQuery.id)
        }
        dao.saveSearchQuery(searchQuery)
    }

    override suspend fun getAllSearchQuery(): List<SearchQuery> {
        return dao.getAllSearchQuery()
    }

    override suspend fun deleteAllSearchQuery() {
        dao.deleteAllSearchQuery()
    }

    override suspend fun deleteSearchQuery(queryPrimaryKey: Long) {
        dao.deleteSearchQuery(queryPrimaryKey)
    }

    private fun compareValues(comparableValue: String, query: String): Boolean {
        return comparableValue.lowercase().replace("-", "")
            .contains(query.lowercase().replace("-", ""))
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