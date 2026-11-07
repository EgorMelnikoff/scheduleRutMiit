package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.rename_schedule

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.NamedScheduleDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = RenameScheduleViewModel.Factory::class
)
class RenameScheduleViewModel @AssistedInject constructor(
    private val namedScheduleDataSource: NamedScheduleDataSource,
    private val appRouter: AppRouter,
    @Assisted
    private val namedScheduleId: Long
) : BaseViewModel<RenameScheduleState, RenameScheduleAction>(RenameScheduleState.Loading) {

    override fun onScreenInitialized() {
        super.onScreenInitialized()
        viewModelScope.launch {
            val namedSchedule = namedScheduleDataSource.getById(namedScheduleId).namedSchedule

            updateState {
                RenameScheduleState.Loaded(
                    currentName = namedSchedule.fullName,
                    newName = namedSchedule.fullName
                )
            }
        }
    }

    override fun onAction(action: RenameScheduleAction) {
        when (action) {
            RenameScheduleAction.OnBackClicked -> onBackClicked()
            is RenameScheduleAction.OnNameChanged -> onChangedName(action.name)
            RenameScheduleAction.OnSaveClicked -> onSaveClicked()
        }
    }

    private fun onBackClicked() {
        appRouter.onBack()
    }

    private fun onChangedName(name: String) {
        updateLoadedState {
            copy(newName = name)
        }
    }

    private fun onSaveClicked() {
        val state = screenState as? RenameScheduleState.Loaded ?: return
        if (!state.renameEnabled) return

        viewModelScope.launch {
            namedScheduleDataSource.updateName(
                namedScheduleId = namedScheduleId,
                newName = state.newName.trim()
            )
            appRouter.onBack()
        }
    }


    private fun updateLoadedState(update: RenameScheduleState.Loaded.() -> RenameScheduleState) {
        updateState {
            if (this is RenameScheduleState.Loaded) this.update() else this
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(namedScheduleId: Long): RenameScheduleViewModel
    }
}