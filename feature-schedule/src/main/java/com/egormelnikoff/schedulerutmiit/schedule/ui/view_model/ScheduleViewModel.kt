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
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.DeleteScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.FetchNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.OpenNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.RenameNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.SaveNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.SetDefaultScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.UpdateEventCommentUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.UpdateEventTagUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.event.UiEvent
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.CurrentState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ReviewUiDto
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleUiDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val resourcesManager: ResourcesManager,

    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    private val fetchNamedScheduleUseCase: FetchNamedScheduleUseCase,
    private val openNamedScheduleUseCase: OpenNamedScheduleUseCase,
    private val saveNamedScheduleUseCase: SaveNamedScheduleUseCase,
    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,
    private val addCustomNamedScheduleUseCase: AddCustomNamedScheduleUseCase,
    private val renameNamedScheduleUseCase: RenameNamedScheduleUseCase,

    private val setDefaultScheduleUseCase: SetDefaultScheduleUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,

    private val eventActionUseCase: EventActionUseCase,
    private val updateEventCommentUseCase: UpdateEventCommentUseCase,
    private val updateEventTagUseCase: UpdateEventTagUseCase
) : ViewModel() {
    private val _currentState = MutableStateFlow(CurrentState())
    private val _namedScheduleState = MutableStateFlow(NamedScheduleState())
    private val _scheduleState = MutableStateFlow(ScheduleState())
    private val _isDataLoading = MutableStateFlow(true)
    private val _uiEventChannel = MutableSharedFlow<UiEvent>()

    val currentState = _currentState.asStateFlow()
    val namedScheduleState = _namedScheduleState.asStateFlow()
    val scheduleState = _scheduleState.asStateFlow()
    val isDataLoading = _isDataLoading.asStateFlow()
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
            updateCurrentState(
                isLoading = false,
                isRefreshing = false,
                isError = false
            )
        }
    }

    fun cancelRefresh() {
        viewModelScope.launch {
            updateScheduleJob?.cancelAndJoin()
            updateCurrentState(
                isLoading = false,
                isRefreshing = false,
                isError = false
            )
        }
    }

    fun refreshScheduleState(
        namedScheduleId: Long? = null,
        updating: Boolean = false,
        showLoading: Boolean = true
    ) {
        val newUpdateScheduleJob = viewModelScope.launch {
            updateScheduleJob?.cancelAndJoin()
            updateCurrentState(
                isLoading = showLoading,
                isRefreshing = updating,
                isError = false
            )

            refreshNamedScheduleUseCase(
                namedScheduleId, updating
            ).let { result ->
                updateNamedScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules
                )
                updateScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules,
                    updateReview = _namedScheduleState.value.namedScheduleWithSchedules?.namedSchedule?.isDefault == true
                )
                updateCurrentState(
                    namedSchedules = result.savedNamedSchedules,
                    isLoading = false,
                    isRefreshing = false,
                    isSaved = result.namedScheduleWithSchedules != null
                )
            }
            if (isDataLoading.value) _isDataLoading.value = false
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

            updateCurrentState(
                isLoading = true
            )
            val result = fetchNamedScheduleUseCase(
                name = name,
                apiId = apiId,
                namedScheduleType = type
            )

            when (val newNamedSchedule = result.namedScheduleWithSchedules) {
                is Result.Success -> {
                    updateNamedScheduleState(
                        namedScheduleWithSchedules = newNamedSchedule.data
                    )
                    updateScheduleState(
                        namedScheduleWithSchedules = result.namedScheduleWithSchedules.data
                    )
                    updateCurrentState(
                        isSaved = result.isSaved,
                        isError = false,
                        isLoading = false
                    )

                }

                is Result.Error -> {
                    updateCurrentState(
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
                updateCurrentState(
                    namedSchedules = if (setDefault) result.savedNamedSchedules else null,
                    isSaved = true
                )
                updateNamedScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules
                )
                updateScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules,
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
                updateCurrentState(
                    namedSchedules = result.savedNamedSchedules,
                    isSaved = true
                )
                updateNamedScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules
                )
                updateScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules,
                    updateReview = result.namedScheduleWithSchedules?.namedSchedule?.isDefault == true
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
                updateCurrentState(
                    namedSchedules = result.savedNamedSchedules
                )

                updateScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules,
                    updateReview = isDefault
                )
                if (currentNamedSchedule().namedSchedule.id == namedScheduleId) {
                    updateNamedScheduleState(
                        namedScheduleWithSchedules = result.namedScheduleWithSchedules
                    )
                }
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
                updateNamedScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules
                )
                updateScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules,
                    updateReview = result.namedScheduleWithSchedules?.namedSchedule?.isDefault == true
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
                updateCurrentState(
                    namedSchedules = result.savedNamedSchedules
                )

                result.namedScheduleWithSchedules?.let {
                    updateNamedScheduleState(
                        namedScheduleWithSchedules = it
                    )
                }
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
                updateCurrentState(
                    namedSchedules = result.savedNamedSchedules,
                    isSaved = true
                )
                updateNamedScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules
                )
                updateScheduleState(
                    namedScheduleWithSchedules = result.namedScheduleWithSchedules,
                    updateReview = result.namedScheduleWithSchedules?.namedSchedule?.isDefault == true
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
                isSaved = _currentState.value.isSaved,
                timetableId = timetableId
            ).let { namedSchedule ->
                updateNamedScheduleState(namedSchedule)
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
                _scheduleState.update { state ->
                    state.copy(
                        scheduleUiDto = state.scheduleUiDto?.copy(
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
                _scheduleState.update { state ->
                    state.copy(
                        scheduleUiDto = state.scheduleUiDto?.copy(
                            eventsExtraData = eventsExtraData
                        )
                    )
                }
            }
        }
    }

    fun eventAction(
        namedScheduleId: Long,
        event: Event,
        eventAction: EventAction
    ) {
        viewModelScope.launch {
            eventActionUseCase(namedScheduleId, event, eventAction).let { result ->
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

    private fun updateCurrentState(
        namedSchedules: List<NamedSchedule>? = null,
        isError: Boolean? = null,
        isRefreshing: Boolean? = null,
        isLoading: Boolean? = null,
        isSaved: Boolean? = null
    ) {
        _currentState.update { state ->
            state.copy(
                namedSchedules = namedSchedules ?: state.namedSchedules,
                isError = isError ?: state.isError,
                isRefreshing = isRefreshing ?: state.isRefreshing,
                isLoading = isLoading ?: state.isLoading,
                isSaved = isSaved ?: state.isSaved
            )
        }
    }

    private fun updateNamedScheduleState(namedScheduleWithSchedules: NamedScheduleWithSchedules?) {
        _namedScheduleState.update { state ->
            state.copy(
                namedScheduleWithSchedules = namedScheduleWithSchedules
            )
        }
    }

    private fun updateScheduleState(
        namedScheduleWithSchedules: NamedScheduleWithSchedules?,
        updateReview: Boolean = false
    ) {
        namedScheduleWithSchedules?.scheduleWithEvents?.findDefaultSchedule()?.let { schedule ->
            val scheduleUiDto = ScheduleUiDto(schedule)
            _scheduleState.update { state ->
                state.copy(
                    scheduleUiDto = scheduleUiDto,
                    reviewUiDto = if (updateReview) {
                        ReviewUiDto.Companion(
                            scheduleUiDto.schedule,
                            scheduleUiDto.periodicEvents,
                            scheduleUiDto.nonPeriodicEvents
                        )
                    } else state.reviewUiDto
                )
            }
            return
        }

        _scheduleState.update { state ->
            state.copy(
                scheduleUiDto = null,
                reviewUiDto = null
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