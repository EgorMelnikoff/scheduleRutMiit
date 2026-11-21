package com.egormelnikoff.schedulerutmiit.tasks.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.time_provider.TimeProvider
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.ObserveAllTasksUseCase
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskAction
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskActionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
internal class TaskViewModel @Inject constructor(
    private val taskActionUseCase: TaskActionUseCase,
    private val appRouter: AppRouter,
    observeAllTasksUseCase: ObserveAllTasksUseCase,
    timeProvider: TimeProvider
) : ViewModel() {


    val hourlyDateTime: StateFlow<LocalDateTime> = timeProvider.currentHour
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        )

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

    fun onEditTaskClicked(taskId: Long, date: LocalDate) {
        appRouter.openDialog(Route.Dialog.EditTaskDialog(taskId, date))
    }

    fun onAddTaskClicked() {
        appRouter.openDialog(Route.Dialog.AddTaskDialog)
    }
}