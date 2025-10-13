package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateFirstDayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ScheduleUiState(
    val savedNamedSchedules: List<NamedScheduleEntity> = emptyList(),
    val defaultScheduleData: ScheduleData? = null,
    val currentScheduleData: ScheduleData? = null,
    val isUpdating: Boolean = false,
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

sealed interface UiEvent {
    data class ShowErrorMessage(val message: String) : UiEvent
    data class ShowInfoMessage(val message: String) : UiEvent
}

interface ScheduleViewModel {
    val uiState: StateFlow<ScheduleUiState>
    val uiEvent: Flow<UiEvent>

    fun loadInitialData(showLoading: Boolean = true)
    fun getNamedScheduleFromApi(name: String, apiId: String, type: Int)
    fun getNamedScheduleFromDb(primaryKeyNamedSchedule: Long, setDefault: Boolean = false)
    fun saveCurrentNamedSchedule()
    fun deleteNamedSchedule(primaryKeyNamedSchedule: Long, isDefault: Boolean)
    fun setDefaultSchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        timetableId: String
    )
    fun updateEventExtra(event: Event, comment: String, tag: Int)
    fun addCustomSchedule(name: String, startDate: LocalDate, endDate: LocalDate)
    fun addCustomEvent(scheduleEntity: ScheduleEntity, event: Event)
    fun updateEventHidden(scheduleEntity: ScheduleEntity, eventPrimaryKey: Long, isHidden: Boolean)
    fun deleteCustomEvent(scheduleEntity: ScheduleEntity, primaryKeyEvent: Long)
}

@HiltViewModel
class ScheduleViewModelImpl @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val resourcesManager: ResourcesManager
) : ViewModel(), ScheduleViewModel {
    private val _uiState = MutableStateFlow(ScheduleUiState())
    private val _uiEventChannel = Channel<UiEvent>()

    override val uiState = _uiState.asStateFlow()
    override val uiEvent = _uiEventChannel.receiveAsFlow()

    private var fetchScheduleJob: Job? = null

    init {
        loadInitialData()
    }

    override fun loadInitialData(showLoading: Boolean) {
        viewModelScope.launch {
            updateUiState(
                isLoading = showLoading,
                isUpdating = false,
                isError = false,
            )
            val savedNamedSchedules = scheduleRepos.getAllNamedSchedules()
            val defaultNamedScheduleEntity = savedNamedSchedules.find { it.isDefault }
                ?: savedNamedSchedules.firstOrNull()

            if (defaultNamedScheduleEntity != null) {
                updateNamedScheduleUiState(
                    namedSchedule = scheduleRepos.getNamedScheduleById(defaultNamedScheduleEntity.id)
                )
            }

            updateUiState(
                savedNamedSchedules = savedNamedSchedules,
                isLoading = false,
                isSaved = defaultNamedScheduleEntity != null,
            )

            defaultNamedScheduleEntity?.let { namedScheduleEntity ->
                updateNamedSchedule(namedScheduleEntity)
                updateNamedScheduleUiState(
                    namedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleEntity.id)
                )
            }
        }
    }

    override fun getNamedScheduleFromApi(
        name: String,
        apiId: String,
        type: Int
    ) {
        val fetchJob = viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()
            updateUiState(isLoading = true)

            val localNamedSchedule = scheduleRepos.getNamedScheduleByApiId(apiId)
            if (localNamedSchedule != null) {
                updateNamedScheduleUiState(
                    namedSchedule = localNamedSchedule
                )
                updateUiState(
                    isError = false,
                    isLoading = false,
                    isSaved = true
                )
                updateNamedSchedule(localNamedSchedule.namedScheduleEntity)
                updateNamedScheduleUiState(
                    namedSchedule = scheduleRepos.getNamedScheduleById(localNamedSchedule.namedScheduleEntity.id)
                )
                return@launch
            }

            when (val newNamedSchedule =
                scheduleRepos.getNamedSchedule(name = name, apiId = apiId, type = type)) {
                is Result.Success -> {
                    updateNamedScheduleUiState(
                        namedSchedule = newNamedSchedule.data
                    )
                    updateUiState(
                        isError = false,
                        isLoading = false,
                        isSaved = false
                    )
                }

                is Result.Error -> {
                    updateUiState(
                        isError = true,
                        isLoading = false
                    )
                    sendErrorUiEvent(
                        message = "${
                            newNamedSchedule.exception.message ?: resourcesManager.getString(
                                R.string.error_load_schedule
                            )
                        }"
                    )
                }
            }
        }
        fetchScheduleJob = fetchJob
    }

    override fun getNamedScheduleFromDb(
        primaryKeyNamedSchedule: Long,
        setDefault: Boolean
    ) {
        viewModelScope.launch {
            if (setDefault) {
                scheduleRepos.updatePriorityNamedSchedule(primaryKeyNamedSchedule)
            }
            val namedSchedule = scheduleRepos.getNamedScheduleById(primaryKeyNamedSchedule)

            if (namedSchedule != null) {
                updateNamedScheduleUiState(
                    namedSchedule = namedSchedule
                )
                updateUiState(
                    isSaved = true,
                    isError = false,
                    isLoading = false,
                    isUpdating = false
                )
                updateNamedSchedule(namedSchedule.namedScheduleEntity)
            }
        }
    }

    override fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            val currentNamedSchedule =
                _uiState.value.currentScheduleData?.namedSchedule ?: return@launch
            if (_uiState.value.isSaved) return@launch

            val namedScheduleId = scheduleRepos.insertNamedSchedule(currentNamedSchedule)

            updateNamedScheduleUiState(
                namedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleId)
            )
            updateUiState(
                savedNamedSchedules = scheduleRepos.getAllNamedSchedules(),
                isSaved = true
            )
        }
    }

    override fun deleteNamedSchedule(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            scheduleRepos.deleteNamedSchedule(primaryKeyNamedSchedule, isDefault)
            val savedNamedSchedules = scheduleRepos.getAllNamedSchedules()
            if (isDefault) {
                val defaultNamedSchedule = savedNamedSchedules.find { it.isDefault }
                    ?: savedNamedSchedules.firstOrNull()
                updateNamedScheduleUiState(
                    namedSchedule = defaultNamedSchedule?.let { scheduleRepos.getNamedScheduleById(it.id) }
                )
            } else {
                _uiState.update {
                    it.copy(
                        currentScheduleData = _uiState.value.defaultScheduleData
                    )
                }
            }
            updateUiState(
                savedNamedSchedules = savedNamedSchedules,
                isSaved = true
            )
        }
    }

    override fun setDefaultSchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        timetableId: String
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentNamedSchedule =
                currentState.currentScheduleData?.namedSchedule ?: return@launch

            if (currentState.isSaved) {
                scheduleRepos.updatePrioritySchedule(
                    primaryKeyNamedSchedule = primaryKeyNamedSchedule,
                    primaryKeySchedule = primaryKeySchedule
                )

                updateNamedScheduleUiState(
                    namedSchedule = scheduleRepos.getNamedScheduleById(primaryKeyNamedSchedule)
                )
            } else {
                val updatedSchedules = currentNamedSchedule.schedules.map { schedule ->
                    schedule.copy(
                        scheduleEntity = schedule.scheduleEntity.copy(
                            isDefault = schedule.scheduleEntity.timetableId == timetableId
                        )
                    )
                }
                updateNamedScheduleUiState(
                    namedSchedule = currentNamedSchedule
                        .copy(schedules = updatedSchedules)
                )
            }
        }
    }

    override fun updateEventExtra(
        event: Event,
        comment: String,
        tag: Int
    ) {
        viewModelScope.launch {
            val namedScheduleId =
                _uiState.value.currentScheduleData?.namedSchedule?.namedScheduleEntity?.id
                    ?: return@launch
            val scheduleId =
                _uiState.value.currentScheduleData?.settledScheduleEntity?.id ?: return@launch

            scheduleRepos.updateEventExtra(
                scheduleId = scheduleId,
                event = event,
                comment = comment,
                tag = tag
            )

            updateNamedScheduleUiState(
                namedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleId),
                scheduleId = scheduleId
            )
        }
    }

    override fun updateEventHidden(
        scheduleEntity: ScheduleEntity,
        eventPrimaryKey: Long,
        isHidden: Boolean
    ) {
        viewModelScope.launch {
            scheduleRepos.updateEventHidden(eventPrimaryKey, isHidden)
            val updatedNamedSchedule = scheduleRepos.getNamedScheduleById(scheduleEntity.namedScheduleId)

            updateNamedScheduleUiState(
                namedSchedule = updatedNamedSchedule,
                scheduleId = scheduleEntity.id
            )
        }
    }

    override fun addCustomSchedule(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
            val fixedStartDate = calculateFirstDayOfWeek(startDate)
            val fixedEndDate = calculateFirstDayOfWeek(endDate).plusDays(6)
            val namedSchedule = NamedScheduleFormatted(
                namedScheduleEntity = NamedScheduleEntity(
                    id = 0,
                    fullName = name,
                    shortName = name,
                    apiId = null,
                    type = 3,
                    isDefault = false,
                    lastTimeUpdate = 0L
                ),
                schedules = listOf(
                    ScheduleFormatted(
                        scheduleEntity = ScheduleEntity(
                            id = 0,
                            namedScheduleId = 0,
                            timetableId = "d=${fixedStartDate}",
                            typeName = resourcesManager.getString(R.string.my)!!,
                            startName = "с $fixedStartDate",
                            downloadUrl = null,
                            startDate = fixedStartDate,
                            endDate = fixedEndDate,
                            recurrence = null
                        ),
                        events = listOf(),
                        eventsExtraData = listOf()
                    )
                )
            )
            val namedScheduleId = scheduleRepos.insertNamedSchedule(namedSchedule)
            updateUiState(
                savedNamedSchedules = scheduleRepos.getAllNamedSchedules()
            )
            updateNamedScheduleUiState(
                namedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleId)
            )
        }
    }

    override fun addCustomEvent(
        scheduleEntity: ScheduleEntity,
        event: Event
    ) {
        viewModelScope.launch {
            if (scheduleRepos.isEventAddingUnavailable(
                    event.startDatetime!!.toLocalDate(),
                    scheduleEntity.id
                )
            ) {
                sendErrorUiEvent(resourcesManager.getString(R.string.events_count_error).toString())
                return@launch
            }
            scheduleRepos.insertEvent(event)
            updateNamedScheduleUiState(
                namedSchedule = scheduleRepos.getNamedScheduleById(scheduleEntity.namedScheduleId),
                scheduleId = scheduleEntity.id
            )
            updateUiState(
                savedNamedSchedules = scheduleRepos.getAllNamedSchedules(),
                isSaved = true
            )
        }
    }

    override fun deleteCustomEvent(
        scheduleEntity: ScheduleEntity,
        primaryKeyEvent: Long
    ) {
        viewModelScope.launch {
            scheduleRepos.deleteEvent(primaryKeyEvent)
            updateNamedScheduleUiState(
                namedSchedule = scheduleRepos.getNamedScheduleById(scheduleEntity.namedScheduleId),
                scheduleId = scheduleEntity.id
            )
        }
    }

    private suspend fun updateNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity
    ) {
        val currentScheduleId = _uiState.value.currentScheduleData?.settledScheduleEntity?.id
        when (val resultUpdate = scheduleRepos.updateNamedSchedule(
            namedScheduleEntity = namedScheduleEntity,
            onStartUpdate = {
                updateUiState(
                    isUpdating = true
                )
            })
        ) {
            is Result.Success -> {
                updateUiState(
                    savedNamedSchedules = scheduleRepos.getAllNamedSchedules()
                )
                if (namedScheduleEntity.id == uiState.value.currentScheduleData?.namedSchedule?.namedScheduleEntity?.id) {
                    updateNamedScheduleUiState(
                        namedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleEntity.id),
                        scheduleId = currentScheduleId
                    )
                }
            }

            is Result.Error -> {
                val errorMessage = resultUpdate.exception.message
                    ?: resourcesManager.getString(R.string.unknown_error)
                sendErrorUiEvent(
                    message = "${resourcesManager.getString(R.string.failed_update)}: $errorMessage"
                )
            }
        }
        updateUiState(
            isUpdating = false
        )
    }


    private fun updateUiState(
        savedNamedSchedules: List<NamedScheduleEntity>? = null,
        isUpdating: Boolean? = null,
        isError: Boolean? = null,
        isLoading: Boolean? = null,
        isSaved: Boolean? = null
    ) {
        _uiState.update {
            it.copy(
                savedNamedSchedules = savedNamedSchedules ?: it.savedNamedSchedules,
                isUpdating = isUpdating ?: it.isUpdating,
                isError = isError ?: it.isError,
                isLoading = isLoading ?: it.isLoading,
                isSaved = isSaved ?: it.isSaved
            )
        }
    }

    private suspend fun sendErrorUiEvent(
        message: String
    ) {
        _uiEventChannel.send(UiEvent.ShowErrorMessage(message))
    }

    private fun updateNamedScheduleUiState(
        namedSchedule: NamedScheduleFormatted?,
        scheduleId: Long? = null
    ) {
        val scheduleData = ScheduleData.calculateScheduleData(
            namedSchedule = namedSchedule,
            scheduleId = scheduleId
        )
        _uiState.update {
            it.copy(
                currentScheduleData = scheduleData
            )
        }
        if (namedSchedule != null && namedSchedule.namedScheduleEntity.isDefault) {
            _uiState.update {
                it.copy(
                    defaultScheduleData = scheduleData
                )
            }
        }
    }
}