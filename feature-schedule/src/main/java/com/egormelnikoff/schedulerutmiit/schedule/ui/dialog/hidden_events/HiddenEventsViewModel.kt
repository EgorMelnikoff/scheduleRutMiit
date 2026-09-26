package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.utils.BaseViewModel
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.observer.ObserveHiddenEventsByScheduleIdUseCase
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
    observeHiddenEventsByScheduleIdUseCase: ObserveHiddenEventsByScheduleIdUseCase,
    private val eventActionUseCase: EventActionUseCase,
    private val appRouter: AppRouter,
    @Assisted
    private val scheduleId: Long
) : BaseViewModel<Unit, HiddenEventsAction>(Unit) {
    val hiddenEvents = observeHiddenEventsByScheduleIdUseCase(scheduleId)
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
            eventActionUseCase(
                EventAction.UpdateHidden(
                    eventId, false
                )
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(scheduleId: Long): HiddenEventsViewModel
    }
}