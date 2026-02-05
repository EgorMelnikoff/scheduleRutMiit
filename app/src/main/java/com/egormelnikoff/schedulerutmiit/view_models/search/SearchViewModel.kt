package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.SearchType
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import com.egormelnikoff.schedulerutmiit.domain.search.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepos: SearchRepos,
    private val searchUseCase: SearchUseCase,
    private val resourcesManager: ResourcesManager
) : ViewModel() {
    private val _searchParams = MutableStateFlow(SearchParams())
    val searchParams = _searchParams.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchParams
                .debounce(500L)
                .distinctUntilChanged()
                .filter { it.query.length > 1 }
                .collect { searchParams ->
                    search(
                        searchParams.query,
                        searchParams.searchType
                    )
                }

            updateSearchQueryHistory()
        }
    }

    fun search(
        query: String,
        searchType: SearchType
    ) {
        viewModelScope.launch {
            _searchState.update { it.copy(isLoading = true) }

            if (query.isNotEmpty()) {
                if (_searchState.value.institutes == null) {
                    loadInstitutes()
                }

                var groupsList = listOf<Group>()
                var peopleList = listOf<Person>()

                val result = searchUseCase(
                    query, searchType, _searchState.value.institutes
                )

                if (result.groups != null) {
                    when (result.groups) {
                        is Result.Error -> {
                            setErrorSearchState(result.groups.typedError)
                            return@launch
                        }

                        is Result.Success -> {
                            groupsList = result.groups.data
                        }
                    }
                }

                if (result.people != null) {
                    when (result.people) {
                        is Result.Error -> {
                            setErrorSearchState(result.people.typedError)
                            return@launch
                        }

                        is Result.Success -> {
                            peopleList = result.people.data
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
    }

    fun saveQueryToHistory(
        searchQuery: SearchQuery
    ) {
        viewModelScope.launch {
            searchRepos.saveSearchQuery(searchQuery)
            updateSearchQueryHistory()
        }
    }

    fun deleteQueryFromHistory(
        queryPrimaryKey: Int
    ) {
        viewModelScope.launch {
            searchRepos.deleteSearchQuery(queryPrimaryKey)
            updateSearchQueryHistory()
        }
    }

    suspend fun updateSearchQueryHistory() {
        _searchState.update {
            it.copy(
                history = searchRepos.getAllSearchQuery()
            )
        }
        println("ntcn")
        println(_searchState.value.history)
    }

    fun setDefaultSearchState() {
        viewModelScope.launch {
            _searchState.update {
                it.copy(
                    history = searchRepos.getAllSearchQuery(),
                    isEmptyQuery = true,
                    isLoading = false,
                    error = null,
                    groups = listOf(),
                    people = listOf()
                )
            }
            _searchParams.value = SearchParams()
        }
    }

    fun changeSearchParams(query: String? = null, searchType: SearchType? = null) {
        _searchParams.update {
            it.copy(
                query = query ?: it.query,
                searchType = searchType ?: it.searchType
            )
        }
    }

    private suspend fun loadInstitutes() {
        when (val institutes = searchRepos.fetchInstitutes()) {
            is Result.Error -> {
                setErrorSearchState(institutes.typedError)
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

    private fun setErrorSearchState(
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
}