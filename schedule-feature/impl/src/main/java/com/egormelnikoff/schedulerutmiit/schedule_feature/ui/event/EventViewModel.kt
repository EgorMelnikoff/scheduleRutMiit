package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.event

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.utils.helper.ClipboardHelper
import com.egormelnikoff.schedulerutmiit.core.common.utils.helper.ShareHelper
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.network.handler.errorHandler
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Group
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.repos.FetchScheduleTrigger
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.EventDataSource
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.LoadEventUseCase
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.UpdateEventCommentUseCase
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.UpdateEventTagUseCase
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
    assistedFactory = EventViewModel.Factory::class
)
class EventViewModel @AssistedInject constructor(
    private val loadEventUseCase: LoadEventUseCase,
    private val eventDataSource: EventDataSource,
    private val updateEventCommentUseCase: UpdateEventCommentUseCase,
    private val updateEventTagUseCase: UpdateEventTagUseCase,

    private val appRouter: AppRouter,
    private val fetchScheduleTrigger: FetchScheduleTrigger,
    private val clipboardHelper: ClipboardHelper,
    private val shareHelper: ShareHelper,
    @Assisted
    private val eventId: Long,
    @Assisted
    private val date: LocalDate?
) : BaseViewModel<EventState, EventAction>(EventState.Loading) {

    private var updateEventCommentJob: Job? = null

    override fun onScreenInitialized() {
        viewModelScope.launch {
            val resultState = loadEventUseCase(eventId, date)
            updateState { resultState }
        }
    }

    override fun onAction(action: EventAction) {
        when (action) {
            is EventAction.OnBackClicked -> onBackCLicked()
            is EventAction.OnGroupClicked -> onGroupClicked(action.group)
            is EventAction.OnLecturerClicked -> onLecturerClicked(action.lecturer)

            is EventAction.OnEditEventClicked -> onEditEventClicked(
                action.eventId,
                action.scheduleId
            )

            is EventAction.OnShowDeleteDialog -> updateLoadedState {
                showDeleteDialog(action.value)
            }

            is EventAction.OnShowHideDialog -> updateLoadedState {
                showHideDialog(action.value)
            }

            is EventAction.OnShowMoreDialog -> updateLoadedState {
                showActionsDialog(action.value)
            }

            is EventAction.OnCommentChanged -> onCommentChanged(action.comment)
            is EventAction.OnTagChanged -> onTagChanged(action.tag)

            is EventAction.OnHideEvent -> onHideEvent(action.eventId)
            is EventAction.OnDeleteEvent -> onDeleteEvent(action.eventId)

            is EventAction.OnGroupLongClicked -> onCopyText(action.group.name)
            is EventAction.OnLecturerLongClicked -> onCopyText(action.lecturer.fullFio)

            is EventAction.OnSendClicked -> onSendClicked(action.text)
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
        launchToCoroutine(
            errorHandler(
                "onGroupClicked"
            ) {
                println("BOOOOM")
            }
        ) {
            fetchScheduleTrigger.trigger(
                name = group.name,
                apiId = group.id,
                type = NamedScheduleType.GROUP
            )
            appRouter.navigateToStartRage()
            appRouter.onBack()
        }
    }

    private fun onLecturerClicked(lecturer: Lecturer) {
        launchToCoroutine(
            errorHandler(
                "onLecturerClicked"
            ) {
                println("BOOOOM")
            }
        ) {
            fetchScheduleTrigger.trigger(
                name = lecturer.shortFio,
                apiId = lecturer.id,
                type = NamedScheduleType.PERSON
            )
            appRouter.navigateToStartRage()
            appRouter.onBack()
        }
    }

    private fun onHideEvent(eventId: Long) {
        viewModelScope.launch {
            eventDataSource.updateIsHidden(eventId, false)
        }
    }

    private fun onDeleteEvent(eventId: Long) {
        viewModelScope.launch {
            eventDataSource.deleteById(eventId)
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
        val state = screenState
        if (state is EventState.Loaded) {
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
        val state = screenState

        if (state is EventState.Loaded) {
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

    private fun updateLoadedState(update: EventState.Loaded.() -> EventState) {
        updateState {
            if (this is EventState.Loaded) this.update() else this
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long, date: LocalDate?): EventViewModel
    }
}