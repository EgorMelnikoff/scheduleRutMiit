package com.egormelnikoff.schedulerutmiit.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.egormelnikoff.schedulerutmiit.AppContainerInterface
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.entity.Group
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteReposInterface
import com.egormelnikoff.schedulerutmiit.model.Institute
import com.egormelnikoff.schedulerutmiit.model.Institutes
import com.egormelnikoff.schedulerutmiit.model.Person
import com.egormelnikoff.schedulerutmiit.ui.search.Options
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed interface InstitutesState {
    data object Empty : InstitutesState
    data class Loaded(
        val institutes: Institutes
    ) : InstitutesState
}

sealed interface SearchState {
    data object EmptyQuery : SearchState
    data object Loading : SearchState
    data class Loaded(
        val groups: List<Group>,
        val people: List<Person>
    ) : SearchState

    data object EmptyResult : SearchState
}

class SearchViewModel(
    private val remoteRepos: RemoteReposInterface
) : ViewModel() {
    companion object {
        fun provideFactory(container: AppContainerInterface): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    return SearchViewModel(
                        remoteRepos = container.remoteRepos
                    ) as T
                }
            }
        }
    }

    private var searchJob: Job? = null
    private val _stateSearch = MutableStateFlow<SearchState>(SearchState.EmptyQuery)
    val stateSearch: StateFlow<SearchState> = _stateSearch

    private val _stateInstitutes = MutableStateFlow<InstitutesState>(InstitutesState.Empty)


    fun search(query: String, selectedOptions: Options) {
        _stateSearch.value = SearchState.Loading
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
                    _stateSearch.value = SearchState.Loaded(
                        groups = groups,
                        people = people,
                    )
                } else {
                    _stateSearch.value = SearchState.EmptyResult
                }
            } else {
                _stateSearch.value = SearchState.EmptyQuery
            }
        }
        searchJob = newSearchJob
    }

    private suspend fun searchGroup(query: String): List<Group> {
        if (_stateInstitutes.value !is InstitutesState.Loaded) {
            when (val institutes = remoteRepos.getInstitutes()) {
                is Result.Error -> {
                    _stateInstitutes.value = InstitutesState.Empty
                }

                is Result.Success -> {
                    _stateInstitutes.value = InstitutesState.Loaded(
                        institutes.data
                    )
                }
            }
        }
        if (_stateInstitutes.value is InstitutesState.Loaded) {
            val groups = (_stateInstitutes.value as InstitutesState.Loaded)
                .institutes.institutes?.let { getGroups(it) }
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
        return when (val professors = remoteRepos.getPeople(query)) {
            is Result.Success -> professors.data
            is Result.Error -> emptyList()
        }
    }

    fun setDefaultSearchState() {
        _stateSearch.value = SearchState.EmptyQuery
    }
}