package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    @Assisted
    private val scheduleId: Long
) : ViewModel() {
    val hiddenEvents = observeHiddenEventsByScheduleIdUseCase(scheduleId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    fun showEvent(eventId: Long) {
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