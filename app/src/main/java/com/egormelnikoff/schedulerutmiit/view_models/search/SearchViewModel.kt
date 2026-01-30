package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.model.Group
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.enums.SearchType
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepos: SearchRepos,
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
                .distinctUntilChangedBy { it.query }
                .filter { it.query.length > 1 }
                .collect { searchParams ->
                    search(
                        searchParams.query,
                        searchParams.searchType
                    )
                }
        }
    }

    fun search(
        query: String,
        searchType: SearchType
    ) {
        _searchState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            if (query.isNotEmpty()) {
                var groupsList = listOf<Group>()
                var peopleList = listOf<Person>()

                if (searchType == SearchType.ALL || searchType == SearchType.GROUPS) {
                    if (_searchState.value.institutes == null) {
                        loadInstitutes()
                    }
                    _searchState.value.institutes?.let {
                        when (
                            val groups = searchRepos.getGroupsByQuery(
                                _searchState.value.institutes!!,
                                query
                            )
                        ) {
                            is Result.Success -> {
                                groupsList = groups.data
                            }

                            is Result.Error -> {
                                setErrorSearchState(groups.typedError)
                                return@launch
                            }
                        }
                    }
                }
                if (searchType == SearchType.ALL || searchType == SearchType.PEOPLE) {
                    when (val people = searchRepos.getPeopleByQuery(query)) {
                        is Result.Success -> {
                            peopleList = people.data
                        }

                        is Result.Error -> {
                            setErrorSearchState(people.typedError)
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

    fun changeSearchParams(query: String? = null, searchType: SearchType? = null) {
        _searchParams.update {
            it.copy(
                query = query ?: it.query,
                searchType = searchType ?: it.searchType
            )
        }
    }

    private suspend fun loadInstitutes() {
        when (val institutes = searchRepos.getInstitutes()) {
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