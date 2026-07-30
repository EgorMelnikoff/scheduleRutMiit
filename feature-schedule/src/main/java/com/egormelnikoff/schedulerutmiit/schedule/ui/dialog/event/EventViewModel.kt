package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.LoadEventUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.UpdateEventCommentUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.UpdateEventTagUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.state.EventState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(
    assistedFactory = EventViewModel.Factory::class
)
class EventViewModel @AssistedInject constructor(
    private val loadEventUseCase: LoadEventUseCase,
    private val eventActionUseCase: EventActionUseCase,
    private val updateEventCommentUseCase: UpdateEventCommentUseCase,
    private val updateEventTagUseCase: UpdateEventTagUseCase,
    @Assisted
    private val eventId: Long,
    @Assisted
    private val date: LocalDate?
) : ViewModel() {
    private val _eventState = MutableStateFlow<EventState>(EventState.Loading)
    val eventState = _eventState.asStateFlow()

    private var updateEventCommentJob: Job? = null

    init {
        viewModelScope.launch {
            _eventState.value = loadEventUseCase(eventId, date)
        }
    }

    fun updateComment(comment: String) {
        _eventState.update { state ->
            when (state) {
                is EventState.Loaded -> state.copy(comment = comment)
                else -> state
            }
        }

        saveComment(comment)
    }

    fun updateTag(tag: Int) {
        _eventState.update { state ->
            when (state) {
                is EventState.Loaded -> state.copy(tag = tag)
                else -> state
            }
        }

        saveTag(tag)
    }

    private fun saveComment(
        comment: String
    ) {
        if (_eventState.value is EventState.Loaded) {
            val state = _eventState.value as EventState.Loaded

            val newUpdateJob = viewModelScope.launch {
                updateEventCommentJob?.cancelAndJoin()
                delay(300.milliseconds)
                updateEventCommentUseCase(
                    date,
                    state.event.scheduleId,
                    state.event,
                    comment
                )
            }
            updateEventCommentJob = newUpdateJob
        }
    }

    private fun saveTag(
        tag: Int
    ) {
        if (_eventState.value is EventState.Loaded) {
            val state = _eventState.value as EventState.Loaded

            viewModelScope.launch {
                updateEventTagUseCase(
                    date,
                    state.event.scheduleId,
                    state.event,
                    tag
                )
            }
        }
    }

    fun eventAction(
        eventAction: EventAction
    ) {
        viewModelScope.launch {
            eventActionUseCase(eventAction)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long, date: LocalDate?): EventViewModel
    }
}