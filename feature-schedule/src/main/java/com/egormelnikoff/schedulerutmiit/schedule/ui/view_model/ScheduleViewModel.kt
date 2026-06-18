package com.egormelnikoff.schedulerutmiit.schedule.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.core.common.resources.getErrorMessage
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.findDefaultSchedule
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.AddCustomNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.FetchNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.ObserveNamedSchedulesUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.OpenNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.RenameNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.SaveNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.SetDefaultScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.UpdateEventCommentUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.UpdateEventTagUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.event.UiEvent
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScreenState
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.DeleteScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ReviewState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val resourcesManager: ResourcesManager,

    observeNamedSchedulesUseCase: ObserveNamedSchedulesUseCase,
    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    private val fetchNamedScheduleUseCase: FetchNamedScheduleUseCase,
    private val openNamedScheduleUseCase: OpenNamedScheduleUseCase,
    private val saveNamedScheduleUseCase: SaveNamedScheduleUseCase,
    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,
    private val addCustomNamedScheduleUseCase: AddCustomNamedScheduleUseCase,
    private val renameNamedScheduleUseCase: RenameNamedScheduleUseCase,

    private val setDefaultScheduleUseCase: SetDefaultScheduleUseCase,

    private val eventActionUseCase: EventActionUseCase,
    private val updateEventCommentUseCase: UpdateEventCommentUseCase,
    private val updateEventTagUseCase: UpdateEventTagUseCase
) : ViewModel() {
    private val _namedScheduleState = MutableStateFlow(NamedScheduleState())
    private val _screenState = MutableStateFlow(ScreenState())
    private val _uiEventChannel = MutableSharedFlow<UiEvent>()

    val namedSchedules: StateFlow<List<NamedSchedule>> = observeNamedSchedulesUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        listOf()
    )
    val scheduleState = _namedScheduleState.asStateFlow()
    val screenState = _screenState.asStateFlow()
    val uiEvent = _uiEventChannel.asSharedFlow()

    private var fetchScheduleJob: Job? = null
    private var updateScheduleJob: Job? = null
    private var updateEventCommentJob: Job? = null


    init {
        refreshScheduleState()
    }

    fun cancelLoading() {
        viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()
            updateScreenState(
                isLoading = false,
                isRefreshing = false,
                isError = false
            )
        }
    }

    fun cancelRefresh() {
        viewModelScope.launch {
            updateScheduleJob?.cancelAndJoin()
            updateScreenState(
                isLoading = false,
                isRefreshing = false,
                isError = false
            )
        }
    }

    fun refreshReview() {
        viewModelScope.launch {
            if (_screenState.value.isSaved) {
                _namedScheduleState.value.scheduleState?.let { scheduleUiDto ->
                    _namedScheduleState.update { state ->
                        state.copy(
                            reviewState = ReviewState(
                                scheduleUiDto.schedule,
                                scheduleUiDto.periodicEvents,
                                scheduleUiDto.nonPeriodicEvents
                            )
                        )
                    }
                }
            }
        }
    }

    fun refreshScheduleState(
        namedScheduleId: Long? = null,
        updating: Boolean = false,
        showLoading: Boolean = true
    ) {
        val newUpdateScheduleJob = viewModelScope.launch {
            updateScheduleJob?.cancelAndJoin()
            updateScreenState(
                isLoading = showLoading,
                isRefreshing = updating,
                isError = false
            )
            refreshNamedScheduleUseCase(
                namedScheduleId, updating
            ).let { result ->
                updateScheduleState(
                    namedScheduleWithSchedules = result,
                    updateReview = result?.namedSchedule?.isDefault == true
                )
                updateScreenState(
                    isLoading = false,
                    isRefreshing = false,
                    isSaved = result != null
                )
            }
        }
        updateScheduleJob = newUpdateScheduleJob
    }

    fun fetchNamedSchedule(
        name: String,
        apiId: Int,
        type: NamedScheduleType
    ) {
        val fetchJob = viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()
            ensureActive()

            updateScreenState(
                isLoading = true
            )
            val result = fetchNamedScheduleUseCase(
                name = name,
                apiId = apiId,
                namedScheduleType = type
            )

            when (val newNamedSchedule = result.namedScheduleWithSchedules) {
                is Result.Success -> {
                    updateScheduleState(
                        namedScheduleWithSchedules = result.namedScheduleWithSchedules.data
                    )
                    updateScreenState(
                        isSaved = result.isSaved,
                        isError = false,
                        isLoading = false
                    )

                }

                is Result.Error -> {
                    updateScreenState(
                        isError = true,
                        isLoading = false
                    )

                    sendErrorUiEvent(
                        message = getErrorMessage(
                            resourcesManager = resourcesManager,
                            typedError = newNamedSchedule.typedError
                        )
                    )
                }
            }
        }
        fetchScheduleJob = fetchJob
    }


    fun setNamedSchedule(
        namedScheduleId: Long,
        setDefault: Boolean = false
    ) {
        viewModelScope.launch {
            if (namedScheduleId == currentNamedSchedule().namedSchedule.id && !setDefault) {
                return@launch
            }

            openNamedScheduleUseCase(
                namedScheduleId, setDefault
            ).let { result ->
                updateScreenState(
                    isSaved = true
                )
                updateScheduleState(
                    namedScheduleWithSchedules = result,
                    updateReview = setDefault
                )
            }
        }
    }

    fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            saveNamedScheduleUseCase(
                currentNamedScheduleWithSchedules = currentNamedSchedule()
            ).let { result ->
                updateScreenState(
                    isSaved = true
                )
                updateScheduleState(
                    namedScheduleWithSchedules = result,
                    updateReview = result.namedSchedule.isDefault
                )
            }
        }
    }

    fun deleteNamedSchedule(
        namedScheduleId: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            deleteNamedScheduleUseCase(
                namedScheduleId, isDefault
            ).let { result ->
                updateScheduleState(
                    namedScheduleWithSchedules = result,
                    updateReview = isDefault
                )
            }
        }
    }

    fun deleteSchedule(
        namedScheduleId: Long,
        scheduleId: Long,
    ) {
        viewModelScope.launch {
            deleteScheduleUseCase(
                namedScheduleId = namedScheduleId,
                currentNamedScheduleId = currentNamedSchedule().namedSchedule.id,
                scheduleId = scheduleId
            ).let { result ->
                updateScheduleState(
                    namedScheduleWithSchedules = result,
                    updateReview = result?.namedSchedule?.isDefault == true
                )
            }
        }
    }

    fun renameNamedSchedule(
        namedScheduleId: Long,
        currentName: String,
        newName: String
    ) {
        viewModelScope.launch {
            if (newName == currentName) {
                return@launch
            }

            renameNamedScheduleUseCase(
                namedScheduleId = namedScheduleId,
                currentNamedScheduleId = currentNamedSchedule().namedSchedule.id,
                newName = newName
            ).let { result ->
                _namedScheduleState.value = _namedScheduleState.value.copy(
                    namedScheduleWithSchedules = result
                )
            }
        }
    }

    fun addCustomNamedSchedule(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
            addCustomNamedScheduleUseCase(
                name, startDate, endDate
            ).let { result ->
                updateScreenState(
                    isSaved = true
                )
                updateScheduleState(
                    namedScheduleWithSchedules = result,
                    updateReview = result.namedSchedule.isDefault
                )
            }
        }
    }


    fun setDefaultSchedule(
        scheduleId: Long,
        timetableId: String
    ) {
        viewModelScope.launch {
            setDefaultScheduleUseCase(
                currentNamedScheduleWithSchedules = currentNamedSchedule(),
                scheduleId = scheduleId,
                isSaved = _screenState.value.isSaved,
                timetableId = timetableId
            ).let { namedSchedule ->
                updateScheduleState(
                    namedScheduleWithSchedules = namedSchedule,
                    updateReview = namedSchedule.namedSchedule.isDefault
                )
            }
        }
    }


    fun updateEventComment(
        scheduleId: Long,
        event: Event,
        dateTime: LocalDateTime,
        comment: String
    ) {
        val newUpdateJob = viewModelScope.launch {
            updateEventCommentJob?.cancelAndJoin()
            delay(300.milliseconds)
            updateEventCommentUseCase(
                dateTime, scheduleId, event, comment
            ).let { eventsExtraData ->
                _namedScheduleState.update { state ->
                    state.copy(
                        scheduleState = state.scheduleState?.copy(
                            eventsExtraData = eventsExtraData
                        )
                    )
                }
            }
        }
        updateEventCommentJob = newUpdateJob
    }

    fun updateEventTag(
        scheduleId: Long,
        event: Event,
        dateTime: LocalDateTime,
        tag: Int
    ) {
        viewModelScope.launch {
            updateEventTagUseCase(
                dateTime, scheduleId, event, tag
            ).let { eventsExtraData ->
                _namedScheduleState.update { state ->
                    state.copy(
                        scheduleState = state.scheduleState?.copy(
                            eventsExtraData = eventsExtraData
                        )
                    )
                }
            }
        }
    }

    fun eventAction(
        namedScheduleId: Long,
        eventAction: EventAction
    ) {
        viewModelScope.launch {
            eventActionUseCase(namedScheduleId, eventAction).let { result ->
                updateScheduleState(
                    namedScheduleWithSchedules = result,
                    updateReview = result.namedSchedule.isDefault
                )
            }
        }
    }

    private fun currentNamedSchedule(): NamedScheduleWithSchedules {
        return requireNotNull(_namedScheduleState.value.namedScheduleWithSchedules)
    }

    private fun updateScreenState(
        isError: Boolean? = null,
        isRefreshing: Boolean? = null,
        isLoading: Boolean? = null,
        isSaved: Boolean? = null
    ) {
        _screenState.update { state ->
            state.copy(
                isError = isError ?: state.isError,
                isRefreshing = isRefreshing ?: state.isRefreshing,
                isLoading = isLoading ?: state.isLoading,
                isSaved = isSaved ?: state.isSaved
            )
        }
    }

    private fun updateScheduleState(
        namedScheduleWithSchedules: NamedScheduleWithSchedules?,
        updateReview: Boolean = false
    ) {
        _namedScheduleState.update { state ->
            val schedule = namedScheduleWithSchedules
                ?.schedulesWithEvents
                ?.findDefaultSchedule()

            val scheduleState = schedule?.let { ScheduleState(it) }

            val reviewState = if (updateReview && scheduleState != null) {
                ReviewState(
                    scheduleState.schedule,
                    scheduleState.periodicEvents,
                    scheduleState.nonPeriodicEvents
                )
            } else state.reviewState

            state.copy(
                namedScheduleWithSchedules = namedScheduleWithSchedules,
                scheduleState = scheduleState,
                reviewState = reviewState
            )
        }
    }

    private suspend fun sendErrorUiEvent(message: String?) {
        _uiEventChannel.emit(
            UiEvent.ErrorMessage(
                message ?: resourcesManager.getString(R.string.unknown_error)
            )
        )
    }
}