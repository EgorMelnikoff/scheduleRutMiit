package com.egormelnikoff.schedulerutmiit.view_models.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.data.Error
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
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

sealed interface UiEvent {
    data class ShowErrorMessage(val message: String) : UiEvent
    data class ShowInfoMessage(val message: String) : UiEvent
}

data class ScheduleUiState(
    val savedNamedSchedules: List<NamedScheduleEntity> = emptyList(),
    val defaultScheduleData: ScheduleData? = null,
    val currentScheduleData: ScheduleData? = null,
    val isUpdating: Boolean = false,
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

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
    private val resourcesManager: ResourcesManager,
    private val workScheduler: WorkScheduler,
    private val widgetDataUpdater: WidgetDataUpdater
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
            val savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules()
            val defaultNamedScheduleEntity = savedNamedSchedules.find { it.isDefault }
                ?: savedNamedSchedules.firstOrNull()

            defaultNamedScheduleEntity?.let {
                updateNamedScheduleUiState(
                    namedSchedule = scheduleRepos.getSavedNamedScheduleById(
                        it.id
                    )
                )
            }

            updateUiState(
                savedNamedSchedules = savedNamedSchedules,
                isLoading = false,
                isSaved = defaultNamedScheduleEntity != null,
            )

            defaultNamedScheduleEntity?.let { namedScheduleEntity ->
                updateNamedScheduleUiState(
                    namedSchedule = scheduleRepos.getSavedNamedScheduleById(namedScheduleEntity.id)
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

            val localNamedSchedule = scheduleRepos.getSavedNamedScheduleByApiId(apiId)
            localNamedSchedule?.let {
                updateNamedScheduleUiState(
                    namedSchedule = it
                )
                updateUiState(
                    isError = false,
                    isLoading = false,
                    isSaved = true
                )
                updateNamedScheduleUiState(
                    namedSchedule = scheduleRepos.getSavedNamedScheduleById(localNamedSchedule.namedScheduleEntity.id)
                )
                return@launch
            }

            when (val newNamedSchedule =
                scheduleRepos.getNewNamedSchedule(name = name, apiId = apiId, type = type)) {
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
                        message = Error.getErrorMessage(
                            resourcesManager = resourcesManager,
                            data = newNamedSchedule.error
                        )
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
                scheduleRepos.updatePrioritySavedNamedSchedules(primaryKeyNamedSchedule)
            }
            val namedSchedule = scheduleRepos.getSavedNamedScheduleById(primaryKeyNamedSchedule)

            namedSchedule?.let {
                if (setDefault) {
                    widgetDataUpdater.updateAll()
                }
                updateNamedScheduleUiState(
                    namedSchedule = it
                )
                updateUiState(
                    savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules(),
                    isSaved = true,
                    isError = false,
                    isLoading = false,
                    isUpdating = false
                )
            }
        }
    }

    override fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            val currentNamedSchedule =
                _uiState.value.currentScheduleData?.namedSchedule ?: return@launch

            val namedScheduleId = scheduleRepos.insertNamedSchedule(currentNamedSchedule)

            val namedSchedule = scheduleRepos.getSavedNamedScheduleById(namedScheduleId)
            if (namedSchedule != null && namedSchedule.namedScheduleEntity.isDefault) {
                workScheduler.startPeriodicScheduleUpdating()
                workScheduler.startPeriodicWidgetUpdating()
            }

            updateNamedScheduleUiState(
                namedSchedule = namedSchedule,

            )
            updateUiState(
                savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules(),
                isSaved = true
            )
        }
    }

    override fun deleteNamedSchedule(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            scheduleRepos.deleteSavedNamedSchedule(primaryKeyNamedSchedule, isDefault)
            val savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules()
            if (savedNamedSchedules.isEmpty()) {
                workScheduler.cancelPeriodicScheduleUpdating()
                workScheduler.cancelPeriodicWidgetUpdating()
            }
            if (isDefault) {
                val defaultNamedSchedule = savedNamedSchedules.find { it.isDefault }
                    ?: savedNamedSchedules.firstOrNull()
                defaultNamedSchedule?.let {
                    updateNamedScheduleUiState(
                        namedSchedule = scheduleRepos.getSavedNamedScheduleById(it.id)
                    )
                }
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
                scheduleRepos.updatePrioritySavedSchedules(
                    primaryKeyNamedSchedule = primaryKeyNamedSchedule,
                    primaryKeySchedule = primaryKeySchedule
                )
                widgetDataUpdater.updateAll()
                updateNamedScheduleUiState(
                    namedSchedule = scheduleRepos.getSavedNamedScheduleById(primaryKeyNamedSchedule)
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
                namedSchedule = scheduleRepos.getSavedNamedScheduleById(namedScheduleId),
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
            val updatedNamedSchedule =
                scheduleRepos.getSavedNamedScheduleById(scheduleEntity.namedScheduleId)

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
            val fixedStartDate = startDate.calculateFirstDayOfWeek()
            val fixedEndDate = endDate.calculateFirstDayOfWeek().plusDays(6)
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
                savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules()
            )
            updateNamedScheduleUiState(
                namedSchedule = scheduleRepos.getSavedNamedScheduleById(namedScheduleId)
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
                namedSchedule = scheduleRepos.getSavedNamedScheduleById(scheduleEntity.namedScheduleId),
                scheduleId = scheduleEntity.id
            )
            updateUiState(
                savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules(),
                isSaved = true
            )
        }
    }

    override fun deleteCustomEvent(
        scheduleEntity: ScheduleEntity,
        primaryKeyEvent: Long
    ) {
        viewModelScope.launch {
            scheduleRepos.deleteSavedEvent(primaryKeyEvent)
            updateNamedScheduleUiState(
                namedSchedule = scheduleRepos.getSavedNamedScheduleById(scheduleEntity.namedScheduleId),
                scheduleId = scheduleEntity.id
            )
        }
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

    private suspend fun sendErrorUiEvent(
        message: String?
    ) {
        _uiEventChannel.send(
            UiEvent.ShowErrorMessage(
                message ?: resourcesManager.getString(R.string.unknown_error)!!
            )
        )
    }
}