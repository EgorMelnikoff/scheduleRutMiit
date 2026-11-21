package com.egormelnikoff.schedulerutmiit.tasks.ui.add_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskAction
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskActionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
internal class AddTaskViewModel @Inject constructor(
    private val taskActionUseCase: TaskActionUseCase,
    private val appRouter: AppRouter
) : ViewModel() {
    private val _addTaskForm = MutableStateFlow(AddTaskForm())
    val addTaskForm = _addTaskForm.asStateFlow()

    fun onBack() {
        appRouter.onBack()
    }

    fun onTextChanged(text: String) {
        updateForm {
            copy(
                text = text
            )
        }
    }

    fun onTagChanged(tag: Int) {
        updateForm {
            copy(
                tag = tag
            )
        }
    }

    fun onTimeChanged(time: LocalTime) {
        updateForm {
            copy(
                time = time
            )
        }
    }

    fun onDatesChanged(startDate: LocalDate, endDate: LocalDate) {
        updateForm {
            copy(
                startDate = startDate,
                endDate = endDate
            )
        }
    }

    fun addTask() {
        viewModelScope.launch {
            taskActionUseCase.invoke(TaskAction.Add(_addTaskForm.value))
            onBack()
        }

    }

    private fun updateForm(
        update: AddTaskForm.() -> AddTaskForm
    ) {
        val form = _addTaskForm.value
        _addTaskForm.value = form.update()
    }

}