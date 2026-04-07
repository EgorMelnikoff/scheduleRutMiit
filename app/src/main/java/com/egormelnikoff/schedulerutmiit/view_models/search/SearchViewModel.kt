package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.enums.SearchType
import com.egormelnikoff.schedulerutmiit.app.dto.remote.person.PersonDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.app.network.result.TypedError
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.datasource.remote.search.SearchRemoteDataSource
import com.egormelnikoff.schedulerutmiit.domain.search.SearchUseCase
import com.egormelnikoff.schedulerutmiit.domain.search.result.SearchResult
import com.egormelnikoff.schedulerutmiit.repos.search_query.SearchQueryRepos
import com.egormelnikoff.schedulerutmiit.view_models.search.state.SearchParams
import com.egormelnikoff.schedulerutmiit.view_models.search.state.SearchState
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
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val searchQueryRepos: SearchQueryRepos,
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
                        _searchState.value.institutesDto
                    )
                }.collect { result ->
                    result?.let { handleSearchResult(it) }
                }
        }
    }

    fun handleSearchResult(
        result: SearchResult
    ) {
        var groupsList = listOf<GroupDto>()
        var peopleList = listOf<PersonDto>()

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
            searchQueryRepos.insert(searchQuery)
            updateSearchQueryHistory()
        }
    }

    fun deleteQueryFromHistory(
        queryId: Long
    ) {
        viewModelScope.launch {
            searchQueryRepos.deleteById(queryId)
            updateSearchQueryHistory()
        }
    }

    suspend fun updateSearchQueryHistory() {
        _searchState.update {
            it.copy(
                history = searchQueryRepos.getAll()
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
            if (_searchState.value.institutesDto == null) {
                loadInstitutes()
            }
        }
    }

    private suspend fun loadInstitutes() {
        when (val institutes = searchRemoteDataSource.fetchInstitutes()) {
            is Result.Error -> {
                setErrorSearchState(institutes.typedError)
            }

            is Result.Success -> {
                _searchState.update {
                    it.copy(
                        institutesDto = institutes.data
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