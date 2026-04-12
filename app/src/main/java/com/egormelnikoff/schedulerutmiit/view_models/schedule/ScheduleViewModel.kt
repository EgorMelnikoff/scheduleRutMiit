package com.egormelnikoff.schedulerutmiit.view_models.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.app.network.result.TypedError
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.domain.schedule.AddCustomNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.DeleteScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.EventAction
import com.egormelnikoff.schedulerutmiit.domain.schedule.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.FetchNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.ManageSchedulesUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.OpenNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.RenameNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.SaveNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.view_models.schedule.event.UiEvent
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.NamedScheduleUiDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    private val manageSchedulesUseCase: ManageSchedulesUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,

    private val eventActionUseCase: EventActionUseCase
) : ViewModel() {
    private val _scheduleState = MutableStateFlow(ScheduleState())
    private val _isDataLoading = MutableStateFlow(true)
    private val _uiEventChannel = MutableSharedFlow<UiEvent>()

    val scheduleState = _scheduleState.asStateFlow()
    val isDataLoading = _isDataLoading.asStateFlow()
    val uiEvent = _uiEventChannel.asSharedFlow()

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

    private fun currentNamedSchedule(): NamedSchedule {
        return _scheduleState.value.currentNamedSchedule?.namedSchedule
            ?: throw NullPointerException()
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

            refreshNamedScheduleUseCase(
                namedScheduleId, updating
            ).let {
                updateState(
                    namedScheduleEntities = it.savedNamedScheduleEntities,
                    namedSchedule = it.namedSchedule,
                    isLoading = false,
                    isRefreshing = false,
                    isSaved = it.namedSchedule != null,
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
            val currentId = currentNamedSchedule().namedScheduleEntity.id
            if (namedScheduleId == currentId && !setDefault) {
                return@launch
            }

            openNamedScheduleUseCase(
                namedScheduleId, setDefault
            ).let {
                updateState(
                    namedScheduleEntities = it.savedNamedScheduleEntities,
                    namedSchedule = it.namedSchedule,
                    isSaved = true,
                    isError = false,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            saveNamedScheduleUseCase(
                currentNamedSchedule()
            ).let {
                updateState(
                    namedScheduleEntities = it.savedNamedScheduleEntities,
                    namedSchedule = it.namedSchedule,
                    isSaved = true
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
                NamedScheduleUiDto.invoke(result.namedSchedule).let { uiDto ->
                    _scheduleState.update {
                        it.copy(
                            savedNamedScheduleEntities = it.savedNamedScheduleEntities,
                            currentNamedSchedule = uiDto,
                            defaultNamedSchedule = uiDto,
                            isSaved = it.savedNamedScheduleEntities.isNotEmpty()
                        )
                    }
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
                namedScheduleId,
                currentNamedSchedule().namedScheduleEntity.id,
                scheduleId
            ).let {
                updateState(
                    namedScheduleEntities = it.savedNamedScheduleEntities,
                    namedSchedule = it.namedSchedule
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
                namedScheduleId,
                currentNamedSchedule().namedScheduleEntity.id,
                newName
            ).let {
                updateState(
                    namedScheduleEntities = it.savedNamedScheduleEntities,
                    namedSchedule = it.namedSchedule
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
            ).let {
                updateState(
                    namedScheduleEntities = it.savedNamedScheduleEntities,
                    namedSchedule = it.namedSchedule,
                    isSaved = true
                )
            }
        }
    }


    fun setDefaultSchedule(
        scheduleId: Long,
        timetableId: String
    ) {
        viewModelScope.launch {
            manageSchedulesUseCase(
                currentNamedSchedule = currentNamedSchedule(),
                scheduleId = scheduleId,
                isSaved = _scheduleState.value.isSaved,
                timetableId = timetableId
            ).let {
                updateState(
                    namedSchedule = it.namedSchedule
                )
            }
        }
    }


    fun updateEventExtra(
        scheduleEntity: ScheduleEntity,
        event: Event,
        comment: String,
        tag: Int
    ) {
        viewModelScope.launch {
            eventActionUseCase(
                scheduleEntity, event, EventAction.UpdateExtra(tag, comment)
            ).let {
                updateState(
                    namedSchedule = it.namedSchedule
                )
            }
        }
    }

    fun eventAction(
        scheduleEntity: ScheduleEntity,
        event: Event,
        eventAction: EventAction
    ) {
        viewModelScope.launch {
            eventActionUseCase(scheduleEntity, event, eventAction).let {
                updateState(
                    namedSchedule = it.namedSchedule
                )
            }
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
                    currentNamedSchedule = namedScheduleUiDto,
                    defaultNamedSchedule = if (namedScheduleUiDto?.namedSchedule?.namedScheduleEntity?.isDefault == true) {
                        namedScheduleUiDto
                    } else it.defaultNamedSchedule,
                )
            }
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