package com.egormelnikoff.schedulerutmiit.tasks.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
import com.egormelnikoff.schedulerutmiit.tasks.data.repos.TaskRepos
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskAction
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskActionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskActionUseCase: TaskActionUseCase,
    taskRepos: TaskRepos
) : ViewModel() {
    private var updateTaskTextJob: Job? = null

    val taskState: StateFlow<Map<LocalDate, List<Task>>> = taskRepos
        .observeAll()
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

    fun updateTaskText(
        updateText: TaskAction.UpdateText
    ) {
        val newUpdateJob = viewModelScope.launch {
            updateTaskTextJob?.cancelAndJoin()
            delay(300.milliseconds)
            taskActionUseCase(updateText)
        }
        updateTaskTextJob = newUpdateJob
    }

}