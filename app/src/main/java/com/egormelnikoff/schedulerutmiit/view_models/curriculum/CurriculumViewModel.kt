package com.egormelnikoff.schedulerutmiit.view_models.curriculum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
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
class CurriculumViewModel @Inject constructor(
    private val searchRepos: SearchRepos,
    private val resourcesManager: ResourcesManager
) : ViewModel() {
    private val _curriculumState = MutableStateFlow(CurriculumState())
    val curriculumState = _curriculumState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    //Допилить фильтры
    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .distinctUntilChanged()
                .filter { it.length > 2 }
                .collect { query ->
                    getSubjectsList(query)
                }
        }
    }

    fun getSubjectsList(
        id: String
    ) {
        viewModelScope.launch {
            _curriculumState.update { it.copy(isLoading = true) }
            if (id.isNotEmpty()) {
                when (val subjectsList = searchRepos.getSubjectsByCurriculum(id)) {
                    is Result.Error -> {
                        setErrorSubjectsState(subjectsList.typedError)
                    }

                    is Result.Success -> {
                        _curriculumState.update {
                            it.copy(
                                subjectsList = subjectsList.data,
                                error = null,
                                isEmptyQuery = false,
                                isLoading = false
                            )
                        }
                    }
                }
            } else setDefaultSubjectsState()
        }
    }

    fun setDefaultSubjectsState() {
        _curriculumState.update {
            it.copy(
                subjectsList = listOf(),
                isEmptyQuery = true,
                isLoading = false,
                error = null
            )
        }
        _searchQuery.value = ""
    }

    fun changeQuery(newValue: String) {
        _searchQuery.value = newValue
    }

    private fun setErrorSubjectsState(
        data: TypedError
    ) {
        _curriculumState.update {
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