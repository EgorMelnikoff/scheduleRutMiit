package com.egormelnikoff.schedulerutmiit.view_models.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

interface ScheduleViewModel {
    val scheduleState: StateFlow<ScheduleState>
    val uiEvent: Flow<UiEvent>
    val isDataLoading: StateFlow<Boolean>
    fun refreshScheduleState(
        showLoading: Boolean = true,
        updating: Boolean = false,
        primaryKeyNamedSchedule: Long? = null
    )

    fun getNamedScheduleFromApi(name: String, apiId: String, type: Int)
    fun getNamedScheduleFromDb(primaryKeyNamedSchedule: Long, setDefault: Boolean = false)
    fun saveCurrentNamedSchedule()
    fun addCustomNamedSchedule(name: String, startDate: LocalDate, endDate: LocalDate)
    fun renameNamedSchedule(namedScheduleEntity: NamedScheduleEntity, newName: String)
    fun setDefaultSchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        timetableId: String
    )

    fun deleteNamedSchedule(primaryKeyNamedSchedule: Long, isDefault: Boolean)
    fun addCustomEvent(scheduleEntity: ScheduleEntity, event: Event)
    fun updateEventExtra(event: Event, comment: String, tag: Int)
    fun updateCustomEvent(scheduleEntity: ScheduleEntity, event: Event)
    fun updateEventHidden(scheduleEntity: ScheduleEntity, eventPrimaryKey: Long, isHidden: Boolean)
    fun deleteCustomEvent(scheduleEntity: ScheduleEntity, eventPrimaryKey: Long)
}

@HiltViewModel
class ScheduleViewModelImpl @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val resourcesManager: ResourcesManager,
    private val workScheduler: WorkScheduler,
    private val widgetDataUpdater: WidgetDataUpdater
) : ViewModel(), ScheduleViewModel {
    private val _scheduleState = MutableStateFlow(ScheduleState())
    override val scheduleState = _scheduleState.asStateFlow()

    private val _uiEventChannel = Channel<UiEvent>()
    override val uiEvent = _uiEventChannel.receiveAsFlow()

    private val _isDataLoading = MutableStateFlow(true)
    override val isDataLoading: StateFlow<Boolean> = _isDataLoading.asStateFlow()

    private var fetchScheduleJob: Job? = null

    init {
        refreshScheduleState()
    }

    override fun refreshScheduleState(
        showLoading: Boolean,
        updating: Boolean,
        primaryKeyNamedSchedule: Long?
    ) {
        viewModelScope.launch {
            updateState(
                isLoading = showLoading,
                isUpdating = updating,
                isError = false,
            )
            val savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules()
            val namedScheduleEntity = savedNamedSchedules.find { entity ->
                primaryKeyNamedSchedule?.let { entity.id == it } ?: entity.isDefault
            } ?: savedNamedSchedules.firstOrNull()

            if (namedScheduleEntity != null && updating) {
                scheduleRepos.updateSavedNamedSchedule(
                    namedScheduleEntity = namedScheduleEntity,
                    onFakeUpdating = { delay(500) }
                )
            }
            updateState(
                savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules(),
                currentNamedSchedule = namedScheduleEntity?.let {
                    scheduleRepos.getSavedNamedScheduleById(
                        primaryKeyNamedSchedule = it.id
                    )
                },
                isLoading = false,
                isUpdating = false,
                isSaved = namedScheduleEntity != null,
            )
            if (isDataLoading.value) _isDataLoading.value = false
        }
    }

    override fun getNamedScheduleFromApi(
        name: String,
        apiId: String,
        type: Int
    ) {
        val fetchJob = viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()
            updateState(isLoading = true)

            val savedNamedSchedule = scheduleRepos.getSavedNamedScheduleByApiId(apiId)
            savedNamedSchedule?.let {
                updateState(
                    currentNamedSchedule = it,
                    isError = false,
                    isLoading = false,
                    isSaved = true
                )
                return@launch
            }

            when (val newNamedSchedule =
                scheduleRepos.getNewNamedSchedule(name = name, apiId = apiId, type = type)) {
                is Result.Success -> {
                    updateState(
                        currentNamedSchedule = newNamedSchedule.data,
                        isError = false,
                        isLoading = false,
                        isSaved = false
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

    override fun getNamedScheduleFromDb(
        primaryKeyNamedSchedule: Long,
        setDefault: Boolean
    ) {
        viewModelScope.launch {
            if (primaryKeyNamedSchedule == _scheduleState.value.currentNamedScheduleData?.namedSchedule?.namedScheduleEntity?.id && !setDefault) {
                return@launch
            }
            if (setDefault) {
                scheduleRepos.updatePrioritySavedNamedSchedules(primaryKeyNamedSchedule)
            }
            val namedSchedule = scheduleRepos.getSavedNamedScheduleById(primaryKeyNamedSchedule)

            namedSchedule?.let {
                if (setDefault) {
                    widgetDataUpdater.updateAll()
                }
                updateState(
                    savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules(),
                    currentNamedSchedule = it,
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
                _scheduleState.value.currentNamedScheduleData?.namedSchedule ?: return@launch

            val namedScheduleId = scheduleRepos.insertNamedSchedule(currentNamedSchedule)
            val namedSchedule = scheduleRepos.getSavedNamedScheduleById(namedScheduleId)

            if (namedSchedule != null && namedSchedule.namedScheduleEntity.isDefault) {
                workScheduler.startPeriodicScheduleUpdating()
            }

            updateState(
                savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules(),
                currentNamedSchedule = namedSchedule,
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
                workScheduler.startPeriodicScheduleUpdating()
                _scheduleState.update {
                    it.copy(
                        savedNamedSchedules = emptyList(),
                        currentNamedScheduleData = null,
                        defaultNamedScheduleData = null
                    )
                }
                return@launch
            }
            if (isDefault) {
                val defaultNamedSchedule = savedNamedSchedules.find { it.isDefault }
                    ?: savedNamedSchedules.firstOrNull()
                defaultNamedSchedule?.let {
                    updateState(
                        currentNamedSchedule = scheduleRepos.getSavedNamedScheduleById(it.id)
                    )
                }
            } else {
                _scheduleState.update {
                    it.copy(
                        currentNamedScheduleData = _scheduleState.value.defaultNamedScheduleData
                    )
                }
            }
            updateState(
                savedNamedSchedules = savedNamedSchedules,
                isSaved = true
            )
        }
    }

    override fun renameNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity,
        newName: String
    ) {
        viewModelScope.launch {
            if (newName == namedScheduleEntity.fullName) {
                return@launch
            }

            scheduleRepos.renameNamedSchedule(
                primaryKeyNamedSchedule = namedScheduleEntity.id,
                type = namedScheduleEntity.type,
                newName = newName
            )

            updateState(
                savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules(),
                currentNamedSchedule = if (namedScheduleEntity.id == _scheduleState.value.currentNamedScheduleData?.namedSchedule?.namedScheduleEntity?.id) {
                    scheduleRepos.getSavedNamedScheduleById(namedScheduleEntity.id)
                } else null
            )
        }
    }

    override fun setDefaultSchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        timetableId: String
    ) {
        viewModelScope.launch {
            val currentNamedSchedule =
                _scheduleState.value.currentNamedScheduleData?.namedSchedule ?: return@launch
            if (_scheduleState.value.isSaved) {
                scheduleRepos.updatePrioritySavedSchedules(
                    primaryKeyNamedSchedule = primaryKeyNamedSchedule,
                    primaryKeySchedule = primaryKeySchedule
                )
                widgetDataUpdater.updateAll()
            }
            val updatedSchedules = currentNamedSchedule.schedules.map { schedule ->
                schedule.copy(
                    scheduleEntity = schedule.scheduleEntity.copy(
                        isDefault = schedule.scheduleEntity.timetableId == timetableId
                    )
                )
            }
            updateState(
                currentNamedSchedule = currentNamedSchedule.copy(schedules = updatedSchedules)
            )
        }
    }

    override fun addCustomNamedSchedule(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
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
                            timetableId = "d=${startDate}",
                            typeName = "Разовое",
                            startName = "с $startDate",
                            downloadUrl = null,
                            startDate = startDate,
                            endDate = endDate,
                            recurrence = null
                        ),
                        events = listOf(),
                        eventsExtraData = listOf()
                    )
                )
            )
            val namedScheduleId = scheduleRepos.insertNamedSchedule(namedSchedule)
            updateState(
                savedNamedSchedules = scheduleRepos.getAllSavedNamedSchedules(),
                currentNamedSchedule = scheduleRepos.getSavedNamedScheduleById(namedScheduleId),
                isSaved = true
            )
        }
    }

    override fun updateEventExtra(
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

            scheduleRepos.updateEventExtra(
                primaryKeySchedule = scheduleId,
                event = event,
                comment = comment,
                tag = tag
            )

            updateState(
                currentNamedSchedule = scheduleRepos.getSavedNamedScheduleById(namedScheduleId)
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
            updateState(
                currentNamedSchedule = scheduleRepos.getSavedNamedScheduleById(scheduleEntity.namedScheduleId)
            )
        }
    }


    override fun updateCustomEvent(scheduleEntity: ScheduleEntity, event: Event) {
        viewModelScope.launch {
            scheduleRepos.deleteSavedEvent(event.id)
            scheduleRepos.insertEvent(event)
            updateState(
                currentNamedSchedule = scheduleRepos.getSavedNamedScheduleById(scheduleEntity.namedScheduleId)
            )
        }
    }

    override fun addCustomEvent(
        scheduleEntity: ScheduleEntity,
        event: Event
    ) {
        viewModelScope.launch {
            if (scheduleRepos.isEventAddingUnavailable(
                    date = event.startDatetime!!.toLocalDate(),
                    scheduleId = scheduleEntity.id
                )
            ) {
                sendErrorUiEvent(resourcesManager.getString(R.string.events_count_error).toString())
                return@launch
            }
            scheduleRepos.insertEvent(event)
            updateState(
                currentNamedSchedule = scheduleRepos.getSavedNamedScheduleById(scheduleEntity.namedScheduleId)
            )
        }
    }

    override fun deleteCustomEvent(
        scheduleEntity: ScheduleEntity,
        eventPrimaryKey: Long
    ) {
        viewModelScope.launch {
            scheduleRepos.deleteSavedEvent(eventPrimaryKey)
            updateState(
                currentNamedSchedule = scheduleRepos.getSavedNamedScheduleById(scheduleEntity.namedScheduleId)
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
                isUpdating = isUpdating ?: it.isUpdating,
                isError = isError ?: it.isError,
                isLoading = isLoading ?: it.isLoading,
                isSaved = isSaved ?: it.isSaved
            )
        }

        currentNamedSchedule?.let {
            val namedScheduleData = NamedScheduleData.namedScheduleData(
                namedSchedule = currentNamedSchedule
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
                message ?: resourcesManager.getString(R.string.unknown_error)!!
            )
        )
    }
}