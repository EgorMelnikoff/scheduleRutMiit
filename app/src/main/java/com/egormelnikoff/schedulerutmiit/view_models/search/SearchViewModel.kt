package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.model.Group
import com.egormelnikoff.schedulerutmiit.app.model.Institute
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.SearchOption
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SearchViewModel {
    val searchState: StateFlow<SearchState>
    val searchParams: StateFlow<SearchParams>
    fun search()
    fun setDefaultSearchState()
    fun changeSearchParams(
        query: String? = null,
        searchOption: SearchOption? = null
    )
}

@Keep
data class SearchParams(
    val query: String = "",
    val searchOption: SearchOption = SearchOption.ALL
)

@Keep
data class SearchState(
    val institutes: Institutes? = null,
    val groups: List<Group> = listOf(),
    val people: List<Person> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModelImpl @Inject constructor(
    private val searchRepos: SearchRepos,
    private val resourcesManager: ResourcesManager
) : ViewModel(), SearchViewModel {
    private val _searchParams = MutableStateFlow(SearchParams())
    override val searchParams = _searchParams.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    override val searchState = _searchState.asStateFlow()

    private var searchJob: Job? = null

    override fun search() {
        _searchState.update { it.copy(isLoading = true) }
        val newSearchJob = viewModelScope.launch {
            searchJob?.cancelAndJoin()
            if (_searchParams.value.query.isNotEmpty()) {
                var groupsList = listOf<Group>()
                var peopleList = listOf<Person>()

                if (_searchParams.value.searchOption == SearchOption.ALL || _searchParams.value.searchOption == SearchOption.GROUPS) {
                    when (val groups = searchGroup(_searchParams.value.query)) {
                        is Result.Success -> {
                            groupsList = groups.data
                        }

                        is Result.Error -> {
                            setErrorState(groups.typedError)
                            return@launch
                        }
                    }
                }
                if (_searchParams.value.searchOption == SearchOption.ALL || _searchParams.value.searchOption == SearchOption.PEOPLE) {
                    when (val people = searchPerson(_searchParams.value.query)) {
                        is Result.Success -> {
                            peopleList = people.data
                        }

                        is Result.Error -> {
                            setErrorState(people.typedError)
                            return@launch
                        }
                    }
                }

                _searchState.update {
                    it.copy(
                        groups = groupsList,
                        people = peopleList,
                        error = null,
                        isEmptyQuery = false,
                        isLoading = false
                    )
                }
            } else {
                setDefaultSearchState()
            }
        }
        searchJob = newSearchJob
    }

    override fun setDefaultSearchState() {
        _searchParams.update {
            it.copy(
                query = "",
                searchOption = SearchOption.ALL
            )
        }
        _searchState.update {
            it.copy(
                isEmptyQuery = true,
                isLoading = false,
                error = null,
                groups = listOf(),
                people = listOf()
            )
        }
    }

    override fun changeSearchParams(query: String?, searchOption: SearchOption?) {
        _searchParams.update {
            it.copy(
                query = query ?: it.query,
                searchOption = searchOption ?: it.searchOption
            )
        }
    }

    fun setErrorState(
        data: TypedError
    ) {
        _searchState.update {
            it.copy(
                error = TypedError.getErrorMessage(
                    resourcesManager = resourcesManager,
                    typedError = data
                ),
                isEmptyQuery = false,
                isLoading = false
            )
        }
    }

    private suspend fun searchGroup(query: String): Result<List<Group>> {
        if (_searchState.value.institutes == null) {
            when (val institutes = searchRepos.getInstitutes()) {
                is Result.Error -> {
                    return institutes
                }

                is Result.Success -> {
                    _searchState.update {
                        it.copy(
                            institutes = institutes.data
                        )
                    }
                }
            }
        }
        _searchState.value.institutes?.let {
            val groups = _searchState.value.institutes!!.institutes?.let { getGroups(it) }
            val filteredGroups = groups?.filter {
                compareValues(it.name ?: "", query)
            } ?: listOf()
            return Result.Success(filteredGroups)
        }
        return Result.Error(TypedError.EmptyBodyError)
    }

    private fun compareValues(comparableValue: String, query: String): Boolean {
        return comparableValue.lowercase().contains(query.lowercase())
    }

    private fun getGroups(institutes: List<Institute>): List<Group> {
        return institutes.flatMap { institute ->
            institute.courses?.flatMap { course ->
                course.specialties?.flatMap { specialty ->
                    specialty.groups ?: emptyList()
                } ?: emptyList()
            } ?: emptyList()
        }
    }

    private suspend fun searchPerson(query: String): Result<List<Person>> {
        return searchRepos.getPeople(query)
    }
}