package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.model.Group
import com.egormelnikoff.schedulerutmiit.app.model.Institute
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.data.Error
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchOption
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
    val uiState: StateFlow<SearchUiState>
    fun search(query: String, selectedSearchOption: SearchOption)
    fun setDefaultSearchState()
}

data class SearchUiState(
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

    private val _uiState = MutableStateFlow(SearchUiState())
    override val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    override fun search(query: String, selectedSearchOption: SearchOption) {
        _uiState.update { it.copy(isLoading = true) }
        val newSearchJob = viewModelScope.launch {
            searchJob?.cancelAndJoin()
            if (query.isNotEmpty()) {
                var groups = listOf<Group>()
                var people = listOf<Person>()

                if (selectedSearchOption == SearchOption.ALL || selectedSearchOption == SearchOption.GROUPS) {
                    val groupsRes = searchGroup(query)
                    when (groupsRes) {
                        is Result.Success -> {
                            groups = groupsRes.data
                        }
                        is Result.Error -> {
                            setErrorState(groupsRes.error)
                            return@launch
                        }
                    }
                }
                if (selectedSearchOption == SearchOption.ALL || selectedSearchOption == SearchOption.PEOPLE) {
                    val peopleRes = searchPerson(query)
                    when (peopleRes) {
                        is Result.Success -> {
                            people = peopleRes.data
                        }
                        is Result.Error -> {
                            setErrorState(peopleRes.error)
                            return@launch
                        }
                    }
                }

                _uiState.update {
                    it.copy(
                        groups = groups,
                        people = people,
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
        _uiState.update {
            it.copy(
                isEmptyQuery = true,
                isLoading = false,
                error = null,
                groups = listOf(),
                people = listOf()
            )
        }
    }

    fun setErrorState (
        data: Error
    ) {
        _uiState.update {
            it.copy(
                error = Error.getErrorMessage(
                    resourcesManager = resourcesManager,
                    data = data
                ),
                isEmptyQuery = false,
                isLoading = false
            )
        }
    }



    private suspend fun searchGroup(query: String): Result<List<Group>> {
        if (_uiState.value.institutes == null) {
            when (val institutes = searchRepos.getInstitutes()) {
                is Result.Error -> {
                    return institutes
                }

                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            institutes = institutes.data
                        )
                    }
                }
            }
        }
        if (_uiState.value.institutes != null) {
            val groups = (_uiState.value.institutes)!!.institutes?.let { getGroups(it) }
            if (groups != null) {
                val filteredGroups = groups
                    .filter {
                        compareValues(it.name ?: "", query)
                    }
                return Result.Success(filteredGroups)
            }
        }
        return Result.Error(Error.EmptyBodyError)
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