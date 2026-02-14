package com.egormelnikoff.schedulerutmiit.view_models.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.AddCustomEventUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.AddCustomNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.FetchNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.ManageSchedulesUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.OpenSavedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.SaveNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.domain.schedule.UpdateEventExtraDataUseCase
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
    private val scheduleRepos: ScheduleRepos,
    private val resourcesManager: ResourcesManager,

    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    private val fetchNamedScheduleUseCase: FetchNamedScheduleUseCase,
    private val openSavedScheduleUseCase: OpenSavedScheduleUseCase,
    private val saveNamedScheduleUseCase: SaveNamedScheduleUseCase,
    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,
    private val addCustomNamedScheduleUseCase: AddCustomNamedScheduleUseCase,

    private val manageSchedulesUseCase: ManageSchedulesUseCase,

    private val updateEventExtraDataUseCase: UpdateEventExtraDataUseCase,
    private val addCustomEventUseCase: AddCustomEventUseCase
) : ViewModel() {
    private val _scheduleState = MutableStateFlow(ScheduleState())
    val scheduleState = _scheduleState.asStateFlow()

    private val _uiEventChannel = Channel<UiEvent>()
    val uiEvent = _uiEventChannel.receiveAsFlow()

    private val _isDataLoading = MutableStateFlow(true)
    val isDataLoading = _isDataLoading.asStateFlow()

    private var fetchScheduleJob: Job? = null

    init {
        refreshScheduleState()
    }

    fun cancelLoading() {
        fetchScheduleJob?.cancel()
        updateState(
            isLoading = false,
            isUpdating = false,
            isError = false
        )
    }

    fun refreshScheduleState(
        primaryKeyNamedSchedule: Long? = null,
        updating: Boolean = false,
        showLoading: Boolean = true
    ) {
        viewModelScope.launch {
            updateState(
                isLoading = showLoading,
                isUpdating = updating,
                isError = false,
            )

            val result = refreshNamedScheduleUseCase(
                primaryKeyNamedSchedule, updating
            )

            if (updating) {
                delay(500)
            }

            updateState(
                savedNamedSchedules = result.savedNamedSchedules,
                currentNamedSchedule = result.namedScheduleFormatted,
                isLoading = false,
                isUpdating = false,
                isSaved = result.isSaved,
            )
            if (isDataLoading.value) _isDataLoading.value = false
        }
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

            when (val newNamedSchedule = result.namedScheduleFormatted) {
                is Result.Success -> {
                    updateState(
                        currentNamedSchedule = newNamedSchedule.data,
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
        primaryKeyNamedSchedule: Long,
        setDefault: Boolean = false
    ) {
        viewModelScope.launch {
            val currentId =
                _scheduleState.value.currentNamedScheduleData?.namedSchedule?.namedScheduleEntity?.id
            if (primaryKeyNamedSchedule == currentId && !setDefault) {
                return@launch
            }

            val result = openSavedScheduleUseCase(
                primaryKeyNamedSchedule, setDefault
            )

            updateState(
                savedNamedSchedules = result.savedNamedSchedules,
                currentNamedSchedule = result.namedScheduleFormatted,
                isSaved = true,
                isError = false,
                isLoading = false,
                isUpdating = false
            )
        }
    }

    fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            val currentNamedSchedule =
                _scheduleState.value.currentNamedScheduleData?.namedSchedule ?: return@launch

            val result = saveNamedScheduleUseCase(
                currentNamedSchedule
            )

            updateState(
                savedNamedSchedules = result.savedNamedSchedules,
                currentNamedSchedule = result.namedScheduleFormatted,
                isSaved = true
            )
        }
    }

    fun deleteNamedSchedule(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            val result = deleteNamedScheduleUseCase(
                primaryKeyNamedSchedule, isDefault
            )
            _scheduleState.update {
                it.copy(
                    savedNamedSchedules = result.savedNamedSchedules ?: listOf(),
                    currentNamedScheduleData = NamedScheduleData.invoke(result.namedScheduleFormatted),
                    isSaved = true
                )
            }
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

            scheduleRepos.updateNamedScheduleName(
                primaryKeyNamedSchedule = namedScheduleEntity.id,
                type = namedScheduleEntity.type,
                newName = newName
            )

            updateState(
                savedNamedSchedules = scheduleRepos.getSavedNamedSchedules(),
                currentNamedSchedule = if (namedScheduleEntity.id == _scheduleState.value.currentNamedScheduleData?.namedSchedule?.namedScheduleEntity?.id) {
                    scheduleRepos.getNamedScheduleById(namedScheduleEntity.id)
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
                savedNamedSchedules = result.savedNamedSchedules,
                currentNamedSchedule = result.namedScheduleFormatted,
                isSaved = true
            )
        }
    }


    fun setDefaultSchedule(
        primaryKeySchedule: Long,
        timetableId: String
    ) {
        viewModelScope.launch {
            val currentNamedSchedule =
                _scheduleState.value.currentNamedScheduleData?.namedSchedule ?: return@launch

            val result = manageSchedulesUseCase(
                currentNamedSchedule = currentNamedSchedule,
                primaryKeySchedule = primaryKeySchedule,
                isSaved = _scheduleState.value.isSaved,
                timetableId = timetableId
            )

            updateState(
                currentNamedSchedule = result.namedScheduleFormatted
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
                _scheduleState.value.currentNamedScheduleData?.namedSchedule?.namedScheduleEntity?.id
                    ?: return@launch
            val scheduleId =
                _scheduleState.value.currentNamedScheduleData?.scheduleData?.scheduleEntity?.id
                    ?: return@launch

            val result = updateEventExtraDataUseCase(
                primaryKeyNamedSchedule = namedScheduleId,
                primaryKeySchedule = scheduleId,
                event = event,
                comment = comment,
                tag = tag
            )

            updateState(
                currentNamedSchedule = result.namedScheduleFormatted
            )
        }
    }

    fun updateEventHidden(
        scheduleEntity: ScheduleEntity,
        eventPrimaryKey: Long,
        isHidden: Boolean
    ) {
        viewModelScope.launch {
            scheduleRepos.updateEventHidden(eventPrimaryKey, isHidden)
            updateState(
                currentNamedSchedule = scheduleRepos.getNamedScheduleById(scheduleEntity.namedScheduleId)
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
                sendErrorUiEvent(resourcesManager.getString(R.string.events_count_error).toString())
                return@launch
            }

            updateState(
                currentNamedSchedule = result.namedScheduleFormatted
            )

        }
    }

    fun deleteCustomEvent(
        scheduleEntity: ScheduleEntity,
        eventPrimaryKey: Long
    ) {
        viewModelScope.launch {
            scheduleRepos.deleteEventById(eventPrimaryKey)

            updateState(
                currentNamedSchedule = scheduleRepos.getNamedScheduleById(scheduleEntity.namedScheduleId)
            )
        }
    }

    fun updateCustomEvent(
        scheduleEntity: ScheduleEntity,
        event: Event
    ) {
        viewModelScope.launch {
            scheduleRepos.updateCustomEvent(event)

            updateState(
                currentNamedSchedule = scheduleRepos.getNamedScheduleById(scheduleEntity.namedScheduleId)
            )
        }
    }


    private fun updateState(
        savedNamedSchedules: List<NamedScheduleEntity>? = null,
        currentNamedSchedule: NamedScheduleFormatted? = null,
        isUpdating: Boolean? = null,
        isError: Boolean? = null,
        isLoading: Boolean? = null,
        isSaved: Boolean? = null
    ) {
        _scheduleState.update {
            it.copy(
                savedNamedSchedules = savedNamedSchedules ?: it.savedNamedSchedules,
                isRefreshing = isUpdating ?: it.isRefreshing,
                isError = isError ?: it.isError,
                isLoading = isLoading ?: it.isLoading,
                isSaved = isSaved ?: it.isSaved
            )
        }

        currentNamedSchedule?.let {
            val namedScheduleData = NamedScheduleData.invoke(
                namedScheduleFormatted = currentNamedSchedule
            )
            _scheduleState.update {
                it.copy(
                    currentNamedScheduleData = namedScheduleData ?: it.currentNamedScheduleData,
                    defaultNamedScheduleData = if (currentNamedSchedule.namedScheduleEntity.isDefault) {
                        namedScheduleData
                    } else it.defaultNamedScheduleData,
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