package com.egormelnikoff.schedulerutmiit.search.ui

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.error.toAppError
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.network.handler.errorHandler
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.sendErrorEvent
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.cache.FetchScheduleCache
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.FetchScheduleEvent
import com.egormelnikoff.schedulerutmiit.search.domain.model.RefreshArgs
import com.egormelnikoff.schedulerutmiit.search.domain.model.SearchQuery
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchRepos
import com.egormelnikoff.schedulerutmiit.search.domain.use_case.SearchUseCase
import com.egormelnikoff.schedulerutmiit.search.utils.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val searchRepos: SearchRepos,
    private val fetchScheduleCache: FetchScheduleCache,
    private val searchUseCase: SearchUseCase,
    private val appRouter: AppRouter,
) : BaseViewModel<SearchState, SearchAction>(SearchState()) {

    val history = searchRepos.observeHistory()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEventFlow.asSharedFlow()

    override fun onScreenInitialized() {
        viewModelScope.launch {
            state
                .map {
                    it.query to it.searchType
                }
                .debounce(300.milliseconds)
                .distinctUntilChanged()
                .mapLatest { params ->
                    if (params.first.isBlank()) {
                        setDefaultSearchState()
                        return@mapLatest null
                    }
                    updateState { copy(isLoading = true) }

                    try {
                        searchUseCase(params)
                    } catch (e: Exception) {
                        _uiEventFlow.sendErrorEvent(e.toAppError())
                        setDefaultSearchState()
                        null
                    }
                }.collect { result ->
                    result?.let {
                        updateState {
                            copy(
                                groups = it.groups ?: listOf(),
                                people = it.people ?: listOf(),
                                isEmptyQuery = false,
                                isLoading = false
                            )
                        }
                    }
                }
        }
    }

    override fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnChangeParams -> changeSearchParams(action.query, action.searchType)
            is SearchAction.OnSaveQuery -> saveQueryToHistory(action.searchQuery)
            is SearchAction.OnDeleteQuery -> deleteQueryFromHistory(action.queryId)
            is SearchAction.OnNavigateSchedule -> navigateSchedule(action.refreshArgs)
            is SearchAction.OnClearParams -> updateState {
                copy(
                    query = "",
                    searchType = SearchType.ALL
                )
            }
        }
    }

    private fun changeSearchParams(query: String?, searchType: SearchType?) {
        updateState {
            copy(
                query = query ?: this.query,
                searchType = searchType ?: this.searchType
            )
        }
    }

    private fun saveQueryToHistory(searchQuery: SearchQuery) {
        launchToCoroutine(
            handler = errorHandler(
                "saveQueryToHistory",
            )
        ) {
            searchRepos.saveToHistory(searchQuery)
        }
    }

    private fun deleteQueryFromHistory(queryId: Long) {
        launchToCoroutine(
            handler = errorHandler(
                tag = "deleteQueryFromHistory"
            )
        ) {
            searchRepos.deleteQueryById(queryId)
        }
    }

    private fun navigateSchedule(refreshArgs: RefreshArgs) {
        launchToCoroutine(
            handler = errorHandler(
                tag = "navigateSchedule"
            )
        ) {
            fetchScheduleCache.addFetchEvent(
                FetchScheduleEvent(
                    apiId = refreshArgs.apiId,
                    name = refreshArgs.name,
                    type = refreshArgs.type
                )
            )
            searchRepos.saveToHistory(
                SearchQuery(
                    apiId = refreshArgs.apiId,
                    name = refreshArgs.name,
                    namedScheduleType = refreshArgs.type
                )
            )
            appRouter.onBack()
            appRouter.navigateToStartRage()
        }
    }

    private fun setDefaultSearchState() {
        updateState {
            copy(
                isEmptyQuery = true,
                isLoading = false,
                groups = listOf(),
                people = listOf()
            )
        }
    }
}
