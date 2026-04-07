package com.egormelnikoff.schedulerutmiit.view_models.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.app.network.result.TypedError
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.domain.schedule.AddCustomEventUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.AddCustomNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.FetchNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.ManageSchedulesUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.OpenNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.SaveNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.UpdateEventExtraDataUseCase
import com.egormelnikoff.schedulerutmiit.repos.event.EventRepos
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.view_models.schedule.event.UiEvent
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.NamedScheduleUiDto
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ScheduleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRepos: ScheduleRepos,
    private val eventRepos: EventRepos,
    private val resourcesManager: ResourcesManager,

    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    private val fetchNamedScheduleUseCase: FetchNamedScheduleUseCase,
    private val openNamedScheduleUseCase: OpenNamedScheduleUseCase,
    private val saveNamedScheduleUseCase: SaveNamedScheduleUseCase,
    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,
    private val addCustomNamedScheduleUseCase: AddCustomNamedScheduleUseCase,

    private val manageSchedulesUseCase: ManageSchedulesUseCase,

    private val updateEventExtraDataUseCase: UpdateEventExtraDataUseCase,
    private val addCustomEventUseCase: AddCustomEventUseCase
) : ViewModel() {
    private val _scheduleState = MutableStateFlow(ScheduleState())
    private val _isDataLoading = MutableStateFlow(true)
    private val _uiEventChannel = Channel<UiEvent>()

    val scheduleState = _scheduleState.asStateFlow()
    val isDataLoading = _isDataLoading.asStateFlow()
    val uiEvent = _uiEventChannel.receiveAsFlow()

    private var fetchScheduleJob: Job? = null
    private var updateScheduleJob: Job? = null

    init {
        refreshScheduleState()
    }

    fun cancelLoading() {
        fetchScheduleJob?.cancel()
        updateState(
            isLoading = false,
            isRefreshing = false,
            isError = false
        )
    }

    fun cancelRefresh() {
        updateScheduleJob?.cancel()
        updateState(
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
            updateState(
                isLoading = showLoading,
                isRefreshing = updating,
                isError = false,
            )

            val result = refreshNamedScheduleUseCase(
                namedScheduleId, updating
            )

            if (updating) {
                delay(500)
            }

            updateState(
                namedScheduleEntities = result.savedNamedScheduleEntities,
                namedSchedule = result.namedSchedule,
                isLoading = false,
                isRefreshing = false,
                isSaved = result.namedSchedule != null,
            )
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
            updateState(isLoading = true)

            val result = fetchNamedScheduleUseCase(
                name = name,
                apiId = apiId,
                namedScheduleType = type
            )

            when (val newNamedSchedule = result.namedSchedule) {
                is Result.Success -> {
                    updateState(
                        namedSchedule = newNamedSchedule.data,
                        isSaved = result.isSaved,
                        isError = false,
                        isLoading = false
                    )
                }

                is Result.Error -> {
                    updateState(
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


    fun getSavedNamedSchedule(
        namedScheduleId: Long,
        setDefault: Boolean = false
    ) {
        viewModelScope.launch {
            val currentId =
                _scheduleState.value.currentNamedSchedule?.namedSchedule?.namedScheduleEntity?.id
            if (namedScheduleId == currentId && !setDefault) {
                return@launch
            }

            val result = openNamedScheduleUseCase(
                namedScheduleId, setDefault
            )

            updateState(
                namedScheduleEntities = result.savedNamedScheduleEntities,
                namedSchedule = result.namedSchedule,
                isSaved = true,
                isError = false,
                isLoading = false,
                isRefreshing = false
            )
        }
    }

    fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            val currentNamedSchedule =
                _scheduleState.value.currentNamedSchedule?.namedSchedule ?: return@launch

            val result = saveNamedScheduleUseCase(
                currentNamedSchedule
            )

            updateState(
                namedScheduleEntities = result.savedNamedScheduleEntities,
                namedSchedule = result.namedSchedule,
                isSaved = true
            )
        }
    }

    fun deleteNamedSchedule(
        namedScheduleId: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            val result = deleteNamedScheduleUseCase(
                namedScheduleId, isDefault
            )
            _scheduleState.update {
                it.copy(
                    savedNamedScheduleEntities = result.savedNamedScheduleEntities ?: listOf(),
                    currentNamedSchedule = NamedScheduleUiDto(result.namedSchedule),
                    isSaved = true
                )
            }
        }
    }

    fun deleteSchedule(
        namedScheduleId: Long,
        scheduleId: Long,
    ) {
        viewModelScope.launch {
            scheduleRepos.deleteById(scheduleId)
            updateState(
                namedScheduleEntities = namedScheduleRepos.getAllEntities(),
                namedSchedule = if (namedScheduleId == _scheduleState.value.currentNamedSchedule?.namedSchedule?.namedScheduleEntity?.id) {
                    namedScheduleRepos.getById(namedScheduleId)
                } else null
            )
        }
    }

    fun renameNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity,
        newName: String
    ) {
        viewModelScope.launch {
            if (newName == namedScheduleEntity.fullName) {
                return@launch
            }

            namedScheduleRepos.updateName(
                namedScheduleId = namedScheduleEntity.id,
                newName = newName
            )

            updateState(
                namedScheduleEntities = namedScheduleRepos.getAllEntities(),
                namedSchedule = if (namedScheduleEntity.id == _scheduleState.value.currentNamedSchedule?.namedSchedule?.namedScheduleEntity?.id) {
                    namedScheduleRepos.getById(namedScheduleEntity.id)
                } else null
            )
        }
    }

    fun addCustomNamedSchedule(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
            val result = addCustomNamedScheduleUseCase(
                name, startDate, endDate
            )
            updateState(
                namedScheduleEntities = result.savedNamedScheduleEntities,
                namedSchedule = result.namedSchedule,
                isSaved = true
            )
        }
    }


    fun setDefaultSchedule(
        scheduleId: Long,
        timetableId: String
    ) {
        viewModelScope.launch {
            val currentNamedSchedule =
                _scheduleState.value.currentNamedSchedule?.namedSchedule ?: return@launch

            val result = manageSchedulesUseCase(
                currentNamedSchedule = currentNamedSchedule,
                scheduleId = scheduleId,
                isSaved = _scheduleState.value.isSaved,
                timetableId = timetableId
            )

            updateState(
                namedSchedule = result.namedSchedule
            )
        }
    }


    fun updateEventExtra(
        event: Event,
        comment: String,
        tag: Int
    ) {
        viewModelScope.launch {
            val namedScheduleId =
                _scheduleState.value.currentNamedSchedule?.namedSchedule?.namedScheduleEntity?.id
                    ?: return@launch
            val scheduleId =
                _scheduleState.value.currentNamedSchedule?.scheduleUiDto?.scheduleEntity?.id
                    ?: return@launch

            val result = updateEventExtraDataUseCase(
                namedScheduleId, scheduleId, event, tag, comment
            )

            updateState(
                namedSchedule = result.namedSchedule
            )
        }
    }

    fun updateEventHidden(
        scheduleEntity: ScheduleEntity,
        eventId: Long,
        isHidden: Boolean
    ) {
        viewModelScope.launch {
            eventRepos.updateIsHidden(eventId, isHidden)
            updateState(
                namedSchedule = namedScheduleRepos.getById(scheduleEntity.namedScheduleId)
            )
        }
    }


    fun addCustomEvent(
        scheduleEntity: ScheduleEntity,
        event: Event
    ) {
        viewModelScope.launch {
            val result = addCustomEventUseCase(
                scheduleEntity, event
            )

            if (result == null) {
                sendErrorUiEvent(resourcesManager.getString(R.string.events_count_error))
                return@launch
            }

            updateState(
                namedSchedule = result.namedSchedule
            )

        }
    }

    fun deleteCustomEvent(
        scheduleEntity: ScheduleEntity,
        eventId: Long
    ) {
        viewModelScope.launch {
            eventRepos.deleteById(eventId)

            updateState(
                namedSchedule = namedScheduleRepos.getById(scheduleEntity.namedScheduleId)
            )
        }
    }

    fun updateCustomEvent(
        scheduleEntity: ScheduleEntity,
        event: Event
    ) {
        viewModelScope.launch {
            eventRepos.update(event)

            updateState(
                namedSchedule = namedScheduleRepos.getById(scheduleEntity.namedScheduleId)
            )
        }
    }


    private fun updateState(
        namedScheduleEntities: List<NamedScheduleEntity>? = null,
        namedSchedule: NamedSchedule? = null,
        isRefreshing: Boolean? = null,
        isError: Boolean? = null,
        isLoading: Boolean? = null,
        isSaved: Boolean? = null
    ) {
        _scheduleState.update {
            it.copy(
                savedNamedScheduleEntities = namedScheduleEntities ?: it.savedNamedScheduleEntities,
                isRefreshing = isRefreshing ?: it.isRefreshing,
                isError = isError ?: it.isError,
                isLoading = isLoading ?: it.isLoading,
                isSaved = isSaved ?: it.isSaved
            )
        }

        namedSchedule?.let {
            val namedScheduleUiDto = NamedScheduleUiDto.invoke(
                namedSchedule = namedSchedule
            )
            _scheduleState.update {
                it.copy(
                    currentNamedSchedule = namedScheduleUiDto ?: it.currentNamedSchedule,
                    defaultNamedSchedule = if (namedSchedule.namedScheduleEntity.isDefault) {
                        namedScheduleUiDto
                    } else it.defaultNamedSchedule,
                )
            }
        }
    }

    private suspend fun sendErrorUiEvent(
        message: String?
    ) {
        _uiEventChannel.send(
            UiEvent.ErrorMessage(
                message ?: resourcesManager.getString(R.string.unknown_error)
            )
        )
    }
}