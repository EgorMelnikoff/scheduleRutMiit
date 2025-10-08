package com.egormelnikoff.schedulerutmiit.ui.view_models.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.entity.Group
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import com.egormelnikoff.schedulerutmiit.model.Institute
import com.egormelnikoff.schedulerutmiit.model.Institutes
import com.egormelnikoff.schedulerutmiit.model.Person
import com.egormelnikoff.schedulerutmiit.ui.dialogs.Options
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
    fun search(query: String, selectedOptions: Options)
    fun setDefaultSearchState()
}

data class SearchUiState(
    val institutes: Institutes? = null,
    val groups: List<Group> = listOf(),
    val people: List<Person> = listOf(),
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModelImpl @Inject constructor(
    private val searchRepos: SearchRepos
) : ViewModel(), SearchViewModel {

    private val _uiState = MutableStateFlow(SearchUiState())
    override val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    override fun search(query: String, selectedOptions: Options) {
        _uiState.update { it.copy(isLoading = true) }
        val newSearchJob = viewModelScope.launch {
            searchJob?.cancelAndJoin()
            if (query.isNotEmpty()) {
                var groups = listOf<Group>()
                var people = listOf<Person>()

                if (selectedOptions == Options.ALL || selectedOptions == Options.GROUPS) {
                    groups = searchGroup(query)
                }
                if (selectedOptions == Options.ALL || selectedOptions == Options.PEOPLE) {
                    people = searchPerson(query)
                }

                if (groups.isNotEmpty() || people.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            groups = groups,
                            people = people,
                            isEmptyQuery = false,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            groups = listOf(),
                            people = listOf(),
                            isEmptyQuery = false,
                            isLoading = false
                        )
                    }
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
                groups = listOf(),
                people = listOf()
            )
        }
    }

    private suspend fun searchGroup(query: String): List<Group> {
        if (_uiState.value.institutes == null) {
            when (val institutes = searchRepos.getInstitutes()) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            institutes = null
                        )
                    }
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
                return filteredGroups
            }
        }
        return emptyList()
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

    private suspend fun searchPerson(query: String): List<Person> {
        return when (val professors = searchRepos.getPeople(query)) {
            is Result.Success -> professors.data
            is Result.Error -> emptyList()
        }
    }
}