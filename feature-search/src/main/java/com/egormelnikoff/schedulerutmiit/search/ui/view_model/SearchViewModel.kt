package com.egormelnikoff.schedulerutmiit.search.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Person
import com.egormelnikoff.schedulerutmiit.core.common.domain.SearchQuery
import com.egormelnikoff.schedulerutmiit.core.common.enums.SearchType
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.ui.event.UiEvent
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchQueryRepos
import com.egormelnikoff.schedulerutmiit.search.domain.use_case.ObserveSearchHistoryUseCase
import com.egormelnikoff.schedulerutmiit.search.domain.use_case.SearchResult
import com.egormelnikoff.schedulerutmiit.search.domain.use_case.SearchUseCase
import com.egormelnikoff.schedulerutmiit.search.ui.view_model.state.SearchParams
import com.egormelnikoff.schedulerutmiit.search.ui.view_model.state.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchQueryRepos: SearchQueryRepos,
    private val searchUseCase: SearchUseCase,
    observeSearchHistoryUseCase: ObserveSearchHistoryUseCase
) : ViewModel() {
    private val _searchParams = MutableStateFlow(SearchParams())
    val searchParams = _searchParams.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    val history = observeSearchHistoryUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            _searchParams.debounce(300.milliseconds)
                .distinctUntilChanged()
                .mapLatest { searchParams ->
                    _searchState.update { it.copy(isLoading = true) }
                    if (searchParams.query.isBlank()) {
                        setDefaultSearchState()
                        return@mapLatest null
                    }
                    searchUseCase(
                        searchParams
                    )
                }.collect { result ->
                    result?.let { handleSearchResult(it) }
                }
        }
    }

    suspend fun handleSearchResult(
        result: SearchResult
    ) {
        var groupsList = listOf<Group>()
        var peopleList = listOf<Person>()

        if (result.groups != null) {
            when (result.groups) {
                is Result.Error -> {
                    _uiEvent.emit(
                        UiEvent.ErrorMessage(result.groups.typedError)
                    )
                    setDefaultSearchState()
                    sendErrorUiEvent(result.groups.typedError)
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
                    _uiEvent.emit(
                        UiEvent.ErrorMessage(result.people.typedError)
                    )
                    setDefaultSearchState()
                    sendErrorUiEvent(result.people.typedError)
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
                typedError = null,
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
        }
    }

    fun deleteQueryFromHistory(
        queryId: Long
    ) {
        viewModelScope.launch {
            searchQueryRepos.deleteById(queryId)
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

    fun setDefaultSearchState() {
        _searchState.update {
            it.copy(
                isEmptyQuery = true,
                isLoading = false,
                typedError = null,
                groups = listOf(),
                people = listOf()
            )
        }
    }

    fun setDefaultParams() {
        _searchParams.value = SearchParams()
    }

    private suspend fun sendErrorUiEvent(typedError: TypedError?) {
        _uiEvent.emit(
            UiEvent.ErrorMessage(typedError ?: TypedError.UnexpectedError())
        )
    }
}