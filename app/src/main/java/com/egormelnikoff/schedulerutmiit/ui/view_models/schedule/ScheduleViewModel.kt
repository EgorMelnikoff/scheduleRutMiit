package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.extension.findDefaultSchedule
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.TypedError
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.AddCustomNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.DeleteScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.EventAction
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.FetchNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.OpenNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.RenameNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.SaveNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.SetDefaultScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.UpdateEventCommentUseCase
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.UpdateEventTagUseCase
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.event.UiEvent
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.CurrentState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ReviewUiDto
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ScheduleUiDto
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
        fetchScheduleJob?.cancel()
        updateCurrentState(
            isLoading = false,
            isRefreshing = false,
            isError = false
        )

    }

    fun cancelRefresh() {
        updateScheduleJob?.cancel()
        updateCurrentState(
            isLoading = false,
            isRefreshing = false,
            isError = false
        )
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
                    namedSchedule = result.namedSchedule
                )
                updateScheduleState(
                    namedSchedule = result.namedSchedule,
                    updateReview = currentNamedSchedule().namedScheduleEntity.isDefault
                )
                updateCurrentState(
                    namedScheduleEntities = result.savedNamedScheduleEntities,
                    isLoading = false,
                    isRefreshing = false,
                    isSaved = result.namedSchedule != null
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

            when (val newNamedSchedule = result.namedSchedule) {
                is Result.Success -> {
                    updateNamedScheduleState(
                        namedSchedule = newNamedSchedule.data
                    )
                    updateScheduleState(
                        namedSchedule = result.namedSchedule.data
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
                        message = TypedError.getErrorMessage(
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
            if (namedScheduleId == currentNamedSchedule().namedScheduleEntity.id && !setDefault) {
                return@launch
            }

            openNamedScheduleUseCase(
                namedScheduleId, setDefault
            ).let { result ->
                if (setDefault) {
                    updateCurrentState(
                        namedScheduleEntities = result.savedNamedScheduleEntities
                    )
                }
                updateNamedScheduleState(
                    namedSchedule = result.namedSchedule
                )
                updateScheduleState(
                    namedSchedule = result.namedSchedule,
                    updateReview = setDefault
                )
            }
        }
    }

    fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            saveNamedScheduleUseCase(
                currentNamedSchedule = currentNamedSchedule()
            ).let { result ->
                updateCurrentState(
                    namedScheduleEntities = result.savedNamedScheduleEntities,
                    isSaved = true
                )
                updateNamedScheduleState(
                    namedSchedule = result.namedSchedule
                )
                updateScheduleState(
                    namedSchedule = result.namedSchedule,
                    updateReview = result.namedSchedule?.namedScheduleEntity?.isDefault == true
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
                    namedScheduleEntities = result.savedNamedScheduleEntities
                )

                updateScheduleState(
                    namedSchedule = result.namedSchedule,
                    updateReview = isDefault
                )
                if (currentNamedSchedule().namedScheduleEntity.id == namedScheduleId) {
                    updateNamedScheduleState(
                        namedSchedule = result.namedSchedule
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
                currentNamedScheduleId = currentNamedSchedule().namedScheduleEntity.id,
                scheduleId = scheduleId
            ).let { result ->
                updateNamedScheduleState(
                    namedSchedule = result.namedSchedule
                )
                updateScheduleState(
                    namedSchedule = result.namedSchedule,
                    updateReview = result.namedSchedule?.namedScheduleEntity?.isDefault == true
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
                currentNamedScheduleId = currentNamedSchedule().namedScheduleEntity.id,
                newName = newName
            ).let { result ->
                updateCurrentState(
                    namedScheduleEntities = result.savedNamedScheduleEntities
                )

                result.namedSchedule?.let {
                    updateNamedScheduleState(
                        namedSchedule = it
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
                    namedScheduleEntities = result.savedNamedScheduleEntities,
                    isSaved = true
                )
                updateNamedScheduleState(
                    namedSchedule = result.namedSchedule
                )
                updateScheduleState(
                    namedSchedule = result.namedSchedule,
                    updateReview = result.namedSchedule?.namedScheduleEntity?.isDefault == true
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
                currentNamedSchedule = currentNamedSchedule(),
                scheduleId = scheduleId,
                isSaved = _currentState.value.isSaved,
                timetableId = timetableId
            ).let { namedSchedule ->
                updateNamedScheduleState(namedSchedule)
                updateScheduleState(
                    namedSchedule = namedSchedule,
                    updateReview = namedSchedule.namedScheduleEntity.isDefault
                )
            }
        }
    }


    fun updateEventComment(
        scheduleEntity: ScheduleEntity,
        event: Event,
        dateTime: LocalDateTime,
        comment: String
    ) {
        val newUpdateJob = viewModelScope.launch {
            updateEventCommentJob?.cancelAndJoin()
            delay(300)
            updateEventCommentUseCase(
                dateTime, scheduleEntity, event, comment
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
        scheduleEntity: ScheduleEntity,
        event: Event,
        dateTime: LocalDateTime,
        tag: Int
    ) {
        viewModelScope.launch {
            updateEventTagUseCase(
                dateTime, scheduleEntity, event, tag
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
        scheduleEntity: ScheduleEntity,
        event: Event,
        eventAction: EventAction
    ) {
        viewModelScope.launch {
            eventActionUseCase(scheduleEntity, event, eventAction).let { result ->
                updateScheduleState(
                    namedSchedule = result,
                    updateReview = result.namedScheduleEntity.isDefault
                )
            }
        }
    }

    private fun currentNamedSchedule(): NamedSchedule {
        return _namedScheduleState.value.namedSchedule
            ?: throw NullPointerException()
    }

    private fun updateCurrentState(
        namedScheduleEntities: List<NamedScheduleEntity>? = null,
        isError: Boolean? = null,
        isRefreshing: Boolean? = null,
        isLoading: Boolean? = null,
        isSaved: Boolean? = null
    ) {
        _currentState.update { state ->
            state.copy(
                namedScheduleEntities = namedScheduleEntities ?: state.namedScheduleEntities,
                isError = isError ?: state.isError,
                isRefreshing = isRefreshing ?: state.isRefreshing,
                isLoading = isLoading ?: state.isLoading,
                isSaved = isSaved ?: state.isSaved
            )
        }
    }

    private fun updateNamedScheduleState(namedSchedule: NamedSchedule?) {
        _namedScheduleState.update { state ->
            state.copy(
                namedSchedule = namedSchedule
            )
        }
    }

    private fun updateScheduleState(namedSchedule: NamedSchedule?, updateReview: Boolean = false) {
        namedSchedule?.schedules?.findDefaultSchedule()?.let { schedule ->
            val scheduleUiDto = ScheduleUiDto(schedule)
            _scheduleState.update { state ->
                state.copy(
                    scheduleUiDto = scheduleUiDto,
                    reviewUiDto = if (updateReview) {
                        ReviewUiDto(
                            scheduleUiDto.scheduleEntity,
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