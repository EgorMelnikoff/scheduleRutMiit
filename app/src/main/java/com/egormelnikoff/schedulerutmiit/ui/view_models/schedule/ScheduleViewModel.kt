package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.egormelnikoff.schedulerutmiit.AppContainer
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.repos.Repos
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateFirstDayOfWeek
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

class ScheduleViewModelImpl(
    private val repos: Repos,
    private val resourcesManager: ResourcesManager
) : ViewModel(), ScheduleViewModel {
    companion object {
        fun provideFactory(container: AppContainer): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    return ScheduleViewModelImpl(
                        repos = container.repos,
                        resourcesManager = container.resourcesManager
                    ) as T
                }
            }
        }
    }

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
                isLoading = showLoading
            )
            val savedNamedSchedules = repos.getAllNamedSchedules()
            val defaultNamedScheduleEntity = savedNamedSchedules.find { it.isDefault }
                ?: savedNamedSchedules.firstOrNull()

            if (defaultNamedScheduleEntity != null) {
                updateNamedScheduleUiState(
                    currentNamedSchedule = repos.getNamedScheduleById(defaultNamedScheduleEntity.id)
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
                    currentNamedSchedule = repos.getNamedScheduleById(namedScheduleEntity.id)
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

            val localNamedSchedule = repos.getNamedScheduleByApiId(apiId)
            if (localNamedSchedule != null) {
                updateNamedScheduleUiState(
                    currentNamedSchedule = localNamedSchedule
                )
                updateUiState(
                    isError = false,
                    isLoading = false,
                    isSaved = true
                )
                updateNamedSchedule(localNamedSchedule.namedScheduleEntity)
                updateNamedScheduleUiState(
                    currentNamedSchedule = repos.getNamedScheduleById(localNamedSchedule.namedScheduleEntity.id)
                )
                return@launch
            }

            when (val newNamedSchedule =
                repos.getNamedSchedule(name = name, apiId = apiId, type = type)) {
                is Result.Success -> {
                    updateNamedScheduleUiState(
                        currentNamedSchedule = newNamedSchedule.data
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
                        message = "${newNamedSchedule.exception.message}"
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
                repos.updatePriorityNamedSchedule(primaryKeyNamedSchedule)
            }
            val namedSchedule = repos.getNamedScheduleById(primaryKeyNamedSchedule)

            if (namedSchedule != null) {
                updateNamedScheduleUiState(
                    currentNamedSchedule = namedSchedule
                )
                updateUiState(
                    isSaved = true
                )
                updateNamedSchedule(namedSchedule.namedScheduleEntity)
            }
        }
    }

    override fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            val currentNamedSchedule = _uiState.value.currentScheduleData?.namedSchedule ?: return@launch
            if (_uiState.value.isSaved) return@launch

            val namedScheduleId = repos.insertNamedSchedule(currentNamedSchedule)

            updateNamedScheduleUiState(
                currentNamedSchedule = repos.getNamedScheduleById(namedScheduleId)
            )
            updateUiState(
                savedNamedSchedules = repos.getAllNamedSchedules(),
                isSaved = true
            )
        }
    }

    override fun deleteNamedSchedule(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            repos.deleteNamedSchedule(primaryKeyNamedSchedule, isDefault)
            val savedNamedSchedules = repos.getAllNamedSchedules()
            if (isDefault) {
                val defaultNamedSchedule = savedNamedSchedules.find { it.isDefault }
                    ?: savedNamedSchedules.firstOrNull()
                updateNamedScheduleUiState(
                    currentNamedSchedule = defaultNamedSchedule?.let { repos.getNamedScheduleById(it.id) }
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
                repos.updatePrioritySchedule(
                    primaryKeyNamedSchedule = primaryKeyNamedSchedule,
                    primaryKeySchedule = primaryKeySchedule
                )

                updateNamedScheduleUiState(
                    currentNamedSchedule = repos.getNamedScheduleById(primaryKeyNamedSchedule)
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
                    currentNamedSchedule = currentNamedSchedule
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

            repos.updateEventExtra(
                scheduleId = scheduleId,
                event = event,
                comment = comment,
                tag = tag
            )

            updateNamedScheduleUiState(
                currentNamedSchedule = repos.getNamedScheduleById(namedScheduleId),
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
            repos.updateEventHidden(eventPrimaryKey, isHidden)
            val updatedNamedSchedule = repos.getNamedScheduleById(scheduleEntity.namedScheduleId)

            updateNamedScheduleUiState(
                currentNamedSchedule = updatedNamedSchedule,
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
            val namedScheduleId = repos.insertNamedSchedule(namedSchedule)
            updateUiState(
                savedNamedSchedules = repos.getAllNamedSchedules()
            )
            updateNamedScheduleUiState(
                currentNamedSchedule = repos.getNamedScheduleById(namedScheduleId)
            )
        }
    }

    override fun addCustomEvent(
        scheduleEntity: ScheduleEntity,
        event: Event
    ) {
        viewModelScope.launch {
            if (repos.isEventAddingUnavailable(
                    event.startDatetime!!.toLocalDate(),
                    scheduleEntity.id
                )
            ) {
                sendErrorUiEvent(resourcesManager.getString(R.string.events_count_error).toString())
                return@launch
            }
            repos.insertEvent(event)
            updateNamedScheduleUiState(
                currentNamedSchedule = repos.getNamedScheduleById(scheduleEntity.namedScheduleId),
                scheduleId = scheduleEntity.id
            )
            updateUiState(
                savedNamedSchedules = repos.getAllNamedSchedules(),
                isSaved = true
            )
        }
    }

    override fun deleteCustomEvent(
        scheduleEntity: ScheduleEntity,
        primaryKeyEvent: Long
    ) {
        viewModelScope.launch {
            repos.deleteEvent(primaryKeyEvent)
            updateNamedScheduleUiState(
                currentNamedSchedule = repos.getNamedScheduleById(scheduleEntity.namedScheduleId),
                scheduleId = scheduleEntity.id
            )
        }
    }

    private suspend fun updateNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity
    ) {
        val currentScheduleId = _uiState.value.currentScheduleData?.settledScheduleEntity?.id
        updateUiState(
            isUpdating = true
        )
        when (val resultUpdate =
            repos.updateNamedSchedule(namedScheduleEntity = namedScheduleEntity)) {
            is Result.Success -> {
                updateUiState(
                    savedNamedSchedules = repos.getAllNamedSchedules()
                )
                if (namedScheduleEntity.id == uiState.value.currentScheduleData?.namedSchedule?.namedScheduleEntity?.id) {
                    updateNamedScheduleUiState(
                        currentNamedSchedule = repos.getNamedScheduleById(namedScheduleEntity.id),
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
        currentNamedSchedule: NamedScheduleFormatted?,
        scheduleId: Long? = null
    ) {
        _uiState.update {
            it.copy(
                currentScheduleData = ScheduleData.calculateScheduleData(
                    namedSchedule = currentNamedSchedule,
                    scheduleId = scheduleId
                )
            )
        }

        if (currentNamedSchedule == null || currentNamedSchedule.namedScheduleEntity.isDefault) {
            _uiState.update {
                it.copy(
                    defaultScheduleData = ScheduleData.calculateScheduleData(
                        namedSchedule = currentNamedSchedule,
                        scheduleId = scheduleId
                    )
                )
            }
        }
    }
}