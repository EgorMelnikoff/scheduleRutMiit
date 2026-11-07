package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.error.toAppError
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.sendErrorEvent
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos.CurriculumRepos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class CurriculumViewModel @Inject constructor(
    private val curriculumRepos: CurriculumRepos
) : BaseViewModel<CurriculumState, CurriculumAction>(CurriculumState()) {

    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEventFlow.asSharedFlow()

    override fun onScreenInitialized() {
        viewModelScope.launch {
            state
                .map { it.searchQuery }
                .debounce(300.milliseconds)
                .distinctUntilChanged()
                .mapLatest { queryId ->
                    if (queryId.length < 3) {
                        updateState {
                            setDefaultState()
                        }
                        return@mapLatest null
                    }
                    updateState { copy(isLoading = true) }

                    try {
                        curriculumRepos.fetchSubjects(queryId, 1)
                    } catch (e: Throwable) {
                        _uiEventFlow.sendErrorEvent(e.toAppError())
                        updateState {
                            setErrorState(e)
                        }
                        null
                    }
                }.collect { result ->
                    result?.let {
                        updateState {
                            setContent(it)
                        }
                    }
                }
        }
    }

    override fun onAction(action: CurriculumAction) {
        when (action) {
            is CurriculumAction.OnChangeQuery -> onChangedQuery(action.query)
            is CurriculumAction.OnClearState -> updateState {
                setDefaultState()
            }
            is CurriculumAction.OnHideAdviceDialog -> updateState {
                copy(
                    showAdviceDialog = false
                )
            }

            is CurriculumAction.OnShowAdviceDialog -> updateState {
                copy(
                    showAdviceDialog = true
                )
            }
        }
    }

    private fun onChangedQuery(newValue: String) {
        updateState {
            copy(searchQuery = newValue)
        }
    }
}