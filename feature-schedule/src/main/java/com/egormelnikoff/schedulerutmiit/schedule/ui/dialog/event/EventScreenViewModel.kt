package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.NavigateSchedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.utils.BaseViewModel
import com.egormelnikoff.schedulerutmiit.core.common.utils.ClipboardHelper
import com.egormelnikoff.schedulerutmiit.core.common.utils.ShareHelper
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.domain.manager.ScheduleManager
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.LoadEventUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.UpdateEventCommentUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.UpdateEventTagUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(
    assistedFactory = EventScreenViewModel.Factory::class
)
class EventScreenViewModel @AssistedInject constructor(
    private val loadEventUseCase: LoadEventUseCase,
    private val eventActionUseCase: EventActionUseCase,
    private val updateEventCommentUseCase: UpdateEventCommentUseCase,
    private val updateEventTagUseCase: UpdateEventTagUseCase,

    private val appRouter: AppRouter,
    private val clipboardHelper: ClipboardHelper,
    private val shareHelper: ShareHelper,
    @Assisted
    private val eventId: Long,
    @Assisted
    private val date: LocalDate?
) : BaseViewModel<EventScreenState, EventScreenAction>(EventScreenState.Loading) {

    private var updateEventCommentJob: Job? = null

    override fun onScreenInitialized() {
        viewModelScope.launch {
            val resultState = loadEventUseCase(eventId, date)
            updateState { resultState }
        }
    }

    override fun onAction(action: EventScreenAction) {
        when (action) {
            is EventScreenAction.OnBackClicked -> onBackCLicked()
            is EventScreenAction.OnGroupClicked -> onGroupClicked(action.group)
            is EventScreenAction.OnLecturerClicked -> onLecturerClicked(action.lecturer)

            is EventScreenAction.OnEditEventClicked -> onEditEventClicked(
                action.eventId,
                action.scheduleId
            )

            is EventScreenAction.OnShowDeleteDialog -> updateLoadedState {
                showDeleteDialog(action.value)
            }

            is EventScreenAction.OnShowHideDialog -> updateLoadedState {
                showHideDialog(action.value)
            }

            is EventScreenAction.OnShowMoreDialog -> updateLoadedState {
                showActionsDialog(action.value)
            }

            is EventScreenAction.OnCommentChanged -> onCommentChanged(action.comment)
            is EventScreenAction.OnTagChanged -> onTagChanged(action.tag)

            is EventScreenAction.OnHideEvent -> onHideEvent(action.eventId)
            is EventScreenAction.OnDeleteEvent -> onDeleteEvent(action.eventId)

            is EventScreenAction.OnGroupLongClicked -> onCopyText(action.group.name)
            is EventScreenAction.OnLecturerLongClicked -> onCopyText(action.lecturer.fullFio)

            is EventScreenAction.OnSendClicked -> onSendClicked(action.text)
        }
    }

    private fun onBackCLicked() {
        appRouter.onBack()
    }

    private fun onCommentChanged(comment: String) {
        updateLoadedState {
            copy(comment = comment)
        }
        saveComment(comment)
    }

    private fun onTagChanged(tag: Int) {
        updateLoadedState {
            copy(tag = tag)
        }
        saveTag(tag)
    }

    private fun onGroupClicked(group: Group) {
        appRouter.navigateToStartRage(
            NavigateSchedule(
                group.name,
                group.id,
                NamedScheduleType.GROUP
            )
        )
        appRouter.onBack()
    }

    private fun onLecturerClicked(lecturer: Lecturer) {
        appRouter.navigateToStartRage(
            NavigateSchedule(
                lecturer.shortFio,
                lecturer.id,
                NamedScheduleType.PERSON
            )
        )
        appRouter.onBack()
    }

    private fun onHideEvent(eventId: Long) {
        viewModelScope.launch {
            eventActionUseCase(EventAction.UpdateHidden(eventId, true))
        }
    }

    private fun onDeleteEvent(eventId: Long) {
        viewModelScope.launch {
            eventActionUseCase(EventAction.Delete(eventId))
        }
    }

    private fun onEditEventClicked(
        eventId: Long, scheduleId: Long
    ) {
        appRouter.onBack()
        appRouter.openDialog(
            Route.Dialog.EditEventDialog(
                eventId, scheduleId
            )
        )
    }

    private fun onCopyText(text: String) {
        clipboardHelper.copyText(text)
    }

    private fun onSendClicked(text: String) {
        shareHelper.shareText(text)
    }

    private fun saveComment(
        comment: String
    ) {
        val state = currentState
        if (state is EventScreenState.Loaded) {
            viewModelScope.launch {
                updateEventCommentJob?.cancelAndJoin()
                delay(300.milliseconds)

                updateEventCommentUseCase(
                    date = date,
                    scheduleId = state.event.scheduleId,
                    event = state.event,
                    comment = comment
                )
            }.also {
                updateEventCommentJob = it
            }
        }
    }

    private fun saveTag(
        tag: Int
    ) {
        updateLoadedState {
            copy(tag = tag)
        }
        val state = currentState

        if (state is EventScreenState.Loaded) {
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

    private fun updateLoadedState(update: EventScreenState.Loaded.() -> EventScreenState) {
        updateState {
            if (this is EventScreenState.Loaded) this.update() else this
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long, date: LocalDate?): EventScreenViewModel
    }
}