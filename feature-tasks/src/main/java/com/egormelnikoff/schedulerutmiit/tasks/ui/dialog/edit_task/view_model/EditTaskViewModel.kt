package com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.LoadTaskUseCase
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskAction
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskActionUseCase
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.view_model.state.EditTaskState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@HiltViewModel(
    assistedFactory = EditTaskViewModel.Factory::class
)
class EditTaskViewModel @AssistedInject constructor(
    private val taskActionUseCase: TaskActionUseCase,
    private val loadTaskUseCase: LoadTaskUseCase,
    @Assisted
    private val taskId: Long,
    @Assisted
    private val date: LocalDate
) : ViewModel() {
    private val _editTaskState = MutableStateFlow<EditTaskState>(EditTaskState.Loading)
    val editTaskState = _editTaskState.asStateFlow()

    init {
        viewModelScope.launch {
            _editTaskState.value = loadTaskUseCase(taskId, date)
        }
    }

    fun updateText(text: String) {
        if (_editTaskState.value is EditTaskState.Loaded) {
            viewModelScope.launch {
                val state = _editTaskState.value as? EditTaskState.Loaded ?: return@launch

                _editTaskState.value = state.copy(
                    editTaskForm = state.editTaskForm.copy(
                        text = text
                    )
                )
            }
        }
    }

    fun updateTime(time: LocalTime) {
        if (_editTaskState.value is EditTaskState.Loaded) {
            viewModelScope.launch {
                val state = (_editTaskState.value as EditTaskState.Loaded)
                _editTaskState.value = state.copy(
                    editTaskForm = state.editTaskForm.copy(
                        time = time
                    )
                )
            }
        }
    }

    fun updateTag(tag: Int) {
        if (_editTaskState.value is EditTaskState.Loaded) {
            viewModelScope.launch {
                val state = (_editTaskState.value as EditTaskState.Loaded)
                _editTaskState.value = state.copy(
                    editTaskForm = state.editTaskForm.copy(
                        tag = tag
                    )
                )
            }
        }
    }

    fun saveChanges() {
        if (_editTaskState.value is EditTaskState.Loaded) {
            viewModelScope.launch {
                val state = (_editTaskState.value as EditTaskState.Loaded)
                taskActionUseCase(
                    TaskAction.UpdateTask(
                        state.task.copy(
                            text = state.editTaskForm.text,
                            time = state.editTaskForm.time,
                            tag = state.editTaskForm.tag
                        )
                    )
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(taskId: Long, date: LocalDate): EditTaskViewModel
    }
}