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
import com.egormelnikoff.schedulerutmiit.domain.search.result.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepos: SearchRepos,
    private val searchUseCase: SearchUseCase,
    private val resourcesManager: ResourcesManager
) : ViewModel() {
    private val institutesMutex = Mutex()

    private val _searchParams = MutableStateFlow(SearchParams())
    val searchParams = _searchParams.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    init {
        viewModelScope.launch {
            updateSearchQueryHistory()
        }

        viewModelScope.launch {
            _searchParams
                .debounce(300L)
                .distinctUntilChangedBy { it.query }
                .mapLatest { searchParams ->
                    _searchState.update { it.copy(isLoading = true) }
                    if (searchParams.query.isBlank()) {
                        setDefaultSearchState()
                        return@mapLatest null
                    }
                    loadInstitutesOnce()
                    searchUseCase(
                        searchParams,
                        _searchState.value.institutes
                    )
                }.collect { result ->
                    result?.let { handleSearchResult(it) }
                }
        }
    }

    fun handleSearchResult(
        result: SearchResult
    ) {
        var groupsList = listOf<Group>()
        var peopleList = listOf<Person>()

        if (result.groups != null) {
            when (result.groups) {
                is Result.Error -> {
                    setErrorSearchState(result.groups.typedError)
                    return
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
                    return
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
        queryPrimaryKey: Long
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
    }

    fun changeSearchParams(query: String? = null, searchType: SearchType? = null) {
        _searchParams.update {
            it.copy(
                query = query ?: it.query,
                searchType = searchType ?: it.searchType
            )
        }
    }

    private suspend fun loadInstitutesOnce() {
        institutesMutex.withLock {
            if (_searchState.value.institutes == null) {
                loadInstitutes()
            }
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

    fun setDefaultSearchState() {
        _searchState.update {
            it.copy(
                isEmptyQuery = true,
                isLoading = false,
                error = null,
                groups = listOf(),
                people = listOf()
            )
        }
        _searchParams.value = SearchParams()
    }

    private fun setErrorSearchState(
        typedError: TypedError
    ) {
        _searchState.update {
            it.copy(
                error = TypedError.getErrorMessage(
                    resourcesManager = resourcesManager,
                    typedError = typedError
                ),
                isEmptyQuery = false,
                isLoading = false
            )
        }
    }
}