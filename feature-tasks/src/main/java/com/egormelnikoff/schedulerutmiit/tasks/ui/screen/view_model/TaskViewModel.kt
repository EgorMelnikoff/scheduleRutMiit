package com.egormelnikoff.schedulerutmiit.tasks.ui.screen.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.ObserveAllTasksUseCase
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskAction
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskActionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskActionUseCase: TaskActionUseCase,
    observeAllTasksUseCase: ObserveAllTasksUseCase
) : ViewModel() {
    val tasks = observeAllTasksUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            mapOf()
        )

    fun taskAction(
        taskAction: TaskAction
    ) {
        viewModelScope.launch {
            taskActionUseCase(taskAction)
        }
    }
}