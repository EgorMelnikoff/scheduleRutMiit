package com.egormelnikoff.schedulerutmiit.view_models.curriculum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.network.model.SubjectModel
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.app.network.result.TypedError
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.domain.subjects.FetchSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CurriculumViewModel @Inject constructor(
    private val fetchSubjectsUseCase: FetchSubjectsUseCase,
    private val resourcesManager: ResourcesManager
) : ViewModel() {
    private val _curriculumState = MutableStateFlow(CurriculumState())
    val curriculumState = _curriculumState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .mapLatest { id ->
                    _curriculumState.update { it.copy(isLoading = true) }
                    if (id.length < 3) {
                        setDefaultSubjectsState()
                        return@mapLatest null
                    }
                    fetchSubjectsUseCase(id)
                }.collect { result ->
                    result?.let { handSubjectsListResult(it) }
                }
        }
    }

    fun handSubjectsListResult(
        result: Result<List<SubjectModel>>
    ) {
        when (result) {
            is Result.Error -> {
                setErrorSubjectsState(result.typedError)
            }

            is Result.Success -> {
                _curriculumState.update {
                    it.copy(
                        subjectsList = result.data,
                        error = null,
                        isEmptyQuery = false,
                        isLoading = false
                    )
                }
            }
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