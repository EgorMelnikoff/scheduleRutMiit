package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.utils.BaseViewModel
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.schedule.domain.manager.ScheduleManager
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.AddCustomNamedScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddScheduleViewModel @Inject constructor(
    private val addCustomNamedScheduleUseCase: AddCustomNamedScheduleUseCase,
    private val appRouter: AppRouter,
    private val scheduleManager: ScheduleManager
) : BaseViewModel<AddScheduleState, AddScheduleAction>(AddScheduleState()) {

    override fun onAction(action: AddScheduleAction) {
        when (action) {
            is AddScheduleAction.OnBackClicked -> onBackClicked()
            is AddScheduleAction.OnSaveClicked -> onSaveClicked()
            is AddScheduleAction.OnUpdateName -> updateState {
                copy(name = action.name)
            }

            is AddScheduleAction.OnUpdateType -> updateState {
                copy(
                    timetableType = action.timetableType
                )
            }

            is AddScheduleAction.OnUpdateDates -> updateState {
                copy(
                    startDate = action.startDate,
                    endDate = action.endDate
                )
            }

            is AddScheduleAction.OnShowBackDialog -> updateState {
                copy(showBackDialog = action.show)
            }

            is AddScheduleAction.OnShowDialogDate -> updateState {
                copy(showDialogDate = action.show)
            }
        }
    }

    private fun onBackClicked() {
        appRouter.onBack()
    }

    private fun onSaveClicked() {
        val state = screenState

        if (!state.isButtonEnabled) return

        viewModelScope.launch {
            val result = addCustomNamedScheduleUseCase(
                state.name.trim(),
                state.startDate!!,
                state.endDate!!,
                state.timetableType
            )

            if (result.second) {
                scheduleManager.openDefault()
            } else {
                scheduleManager.openSaved(result.first)
            }
        }
    }
}