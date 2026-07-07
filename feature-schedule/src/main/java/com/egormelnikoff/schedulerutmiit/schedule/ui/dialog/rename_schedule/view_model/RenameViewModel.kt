package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.RenameNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.view_model.state.RenameState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = RenameViewModel.Factory::class
)
class RenameViewModel @AssistedInject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val renameNamedScheduleUseCase: RenameNamedScheduleUseCase,
    @Assisted
    private val namedScheduleId: Long
) : ViewModel() {
    private val _renameState = MutableStateFlow<RenameState>(RenameState.Loading)
    val renameState = _renameState.asStateFlow()

    init {
        viewModelScope.launch {
            val namedSchedule = namedScheduleRepos.getById(namedScheduleId).namedSchedule
            _renameState.value = RenameState.Loaded(
                currentName = namedSchedule.fullName,
                newName = namedSchedule.fullName
            )
        }
    }

    fun updateName(name: String) {
        _renameState.update { state ->
            when (state) {
                is RenameState.Loaded ->
                    state.copy(newName = name)

                RenameState.Loading -> state
            }
        }
    }

    fun renameNamedSchedule() {
        val state = _renameState.value as? RenameState.Loaded ?: return

        if (!state.renameEnabled) return

        viewModelScope.launch {
            renameNamedScheduleUseCase(
                namedScheduleId = namedScheduleId,
                newName = state.newName.trim()
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(namedScheduleId: Long): RenameViewModel
    }
}