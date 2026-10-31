package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.hidden_events

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.EventDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = HiddenEventsViewModel.Factory::class
)
class HiddenEventsViewModel @AssistedInject constructor(
    private val eventDataSource: EventDataSource,
    private val appRouter: AppRouter,
    @Assisted
    private val scheduleId: Long
) : BaseViewModel<Unit, HiddenEventsAction>(Unit) {
    val hiddenEvents = eventDataSource.observeHiddenEvents(scheduleId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    override fun onAction(action: HiddenEventsAction) {
        when (action) {
            is HiddenEventsAction.OnBackClicked -> onBackClicked()
            is HiddenEventsAction.OnShowEvent -> onShowEvent(action.id)
        }
    }

    private fun onBackClicked() {
        appRouter.onBack()
    }

    private fun onShowEvent(eventId: Long) {
        viewModelScope.launch {
            eventDataSource.updateIsHidden(
                eventId, false
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(scheduleId: Long): HiddenEventsViewModel
    }
}