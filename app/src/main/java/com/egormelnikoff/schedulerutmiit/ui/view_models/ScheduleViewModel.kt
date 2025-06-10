package com.egormelnikoff.schedulerutmiit.ui.view_models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.repos.Result
import com.egormelnikoff.schedulerutmiit.data.repos.local.LocalRepos
import com.egormelnikoff.schedulerutmiit.data.repos.local.database.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.Event
import com.egormelnikoff.schedulerutmiit.data.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteRepos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface SchedulesState {
    data object EmptyBase : SchedulesState
    data class Loaded(
        val savedSchedules: MutableList<NamedScheduleFormatted> = mutableListOf()
    ) : SchedulesState
}

sealed interface ScheduleState {
    data object EmptyBase : ScheduleState
    data object Loading : ScheduleState
    data object Error : ScheduleState
    data class Loaded(
        val namedSchedule: NamedScheduleFormatted,
        val selectedSchedule: ScheduleFormatted?,
        val isSaved: Boolean,
        val isSavingAvailable: Boolean,
        val message: String? = null
    ) : ScheduleState
}


class ScheduleViewModel(private val context: Context) : ViewModel() {
    private val db = AppDatabase.getDatabase(context)
    private val namedScheduleDao = db.namedScheduleDao()

    private val localRepos = LocalRepos(namedScheduleDao)

    private val _stateSchedules = MutableStateFlow<SchedulesState>(SchedulesState.EmptyBase)
    val stateSchedules: StateFlow<SchedulesState> = _stateSchedules

    private val _stateSchedule = MutableStateFlow<ScheduleState>(ScheduleState.EmptyBase)
    val stateSchedule: StateFlow<ScheduleState> = _stateSchedule

    init {
        viewModelScope.launch {
            val defaultNamedSchedule = load()
            if (defaultNamedSchedule != null) {
                getUpdatedNamedSchedule(defaultNamedSchedule)
            }

        }
    }


    suspend fun load(): NamedScheduleFormatted? {
        val namedSchedules = localRepos.getAll()
        return if (namedSchedules.isEmpty()) {
            _stateSchedule.value = ScheduleState.EmptyBase
            null
        } else {
            val defaultNamedSchedule = namedSchedules.find { it.namedScheduleEntity.isDefault }
                ?: namedSchedules.first()
            loadSchedules(namedSchedules)
            loadSchedule(
                namedSchedule = defaultNamedSchedule,
                isSaved = true
            )
            defaultNamedSchedule
        }
    }

    private suspend fun loadSchedule(
        namedSchedule: NamedScheduleFormatted,
        isSaved: Boolean
    ) {
        _stateSchedule.value = ScheduleState.Loaded(
            namedSchedule = namedSchedule,
            selectedSchedule = namedSchedule.schedules.find { it.scheduleEntity.isDefault }
                ?: namedSchedule.schedules.firstOrNull(),
            isSaved = isSaved,
            isSavingAvailable = localRepos.getCount() < 5,
        )
    }

    private fun loadSchedules(
        namedSchedules: MutableList<NamedScheduleFormatted>
    ) {
        if (namedSchedules.isEmpty()) {
            _stateSchedules.value = SchedulesState.EmptyBase
        } else {
            _stateSchedules.value = SchedulesState.Loaded(
                savedSchedules = namedSchedules
            )
        }
    }

    private suspend fun getUpdatedNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ) {
        if (System.currentTimeMillis() - namedSchedule.namedScheduleEntity.lastTimeUpdate > 43200000) {
            val updatedNamedSchedule = RemoteRepos.getNamedSchedule(
                namedScheduleId = namedSchedule.namedScheduleEntity.id,
                name = namedSchedule.namedScheduleEntity.shortName,
                apiId = namedSchedule.namedScheduleEntity.apiId,
                type = namedSchedule.namedScheduleEntity.type
            )

            when (updatedNamedSchedule) {
                is Result.Error -> {
                    _stateSchedule.value = (stateSchedule.value as ScheduleState.Loaded).copy(
                        message = "${context.getString(R.string.failed_update_schedules)}\n${updatedNamedSchedule.exception.message}"
                    )
                }

                is Result.Success -> {
                    val oldSchedulesMap = namedSchedule.schedules.associateBy {
                        it.scheduleEntity.timetableId
                    }
                    updatedNamedSchedule.data.schedules.forEach { updatedSchedule ->
                        val oldSchedule =
                            oldSchedulesMap[updatedSchedule.scheduleEntity.timetableId]
                        if (oldSchedule != null) {
                            val updatedEvents = updatedSchedule.events.map { event ->
                                val oldEvent = oldSchedule.events.find { it == event }
                                event.copy(
                                    id = oldEvent?.id ?: 0L
                                )
                            }
                            val ids = updatedEvents.map { it.id }.filter { it != 0L }
                            if (ids.size == ids.toSet().size) {
                                val updatedScheduleWithId = ScheduleFormatted(
                                    events = updatedEvents,
                                    scheduleEntity = oldSchedule.scheduleEntity,
                                    eventsExtraData = oldSchedule.eventsExtraData
                                )
                                localRepos.deleteSchedule(oldSchedule.scheduleEntity.id)
                                localRepos.insertSchedule(
                                    namedSchedule.namedScheduleEntity.id,
                                    updatedScheduleWithId
                                )
                            } else {
                                _stateSchedule.value = (stateSchedule.value as ScheduleState.Loaded).copy(
                                    message = context.getString(R.string.failed_update)
                                )
                            }
                        } else {
                            localRepos.insertSchedule(
                                namedSchedule.namedScheduleEntity.id,
                                updatedSchedule
                            )
                        }
                    }

                    namedSchedule.schedules.forEach { oldSchedule ->
                        if (!updatedNamedSchedule.data.schedules.any { it.scheduleEntity.timetableId == oldSchedule.scheduleEntity.timetableId }
                            && LocalDate.now() in oldSchedule.scheduleEntity.startDate..oldSchedule.scheduleEntity.endDate
                        ) {
                            localRepos.deleteSchedule(
                                id = oldSchedule.scheduleEntity.id,
                                deleteEventsExtra = true
                            )
                        }
                    }
                    load()
                }
            }
        }
    }


    fun deleteNamedSchedule(
        primaryKey: Long,
        isDefault: Boolean,
    ) {
        viewModelScope.launch {
            localRepos.deleteSavedSchedule(primaryKey, isDefault)
            loadSchedules(localRepos.getAll())

            val state = _stateSchedule.value as ScheduleState.Loaded
            _stateSchedule.value = state.copy(
                isSaved = (primaryKey != state.namedSchedule.namedScheduleEntity.id) && state.isSaved,
                isSavingAvailable = localRepos.getCount() < 5
            )
        }
    }


    fun getSchedule(
        name: String,
        apiId: String,
        type: Int
    ) {
        viewModelScope.launch {
            _stateSchedule.value = ScheduleState.Loading
            val checkedNamedSchedule = localRepos.getNamedScheduleByApiId(apiId)
            if (checkedNamedSchedule != null) {
                loadSchedule(
                    namedSchedule = checkedNamedSchedule,
                    isSaved = true
                )
                return@launch
            }
            when (val namedSchedule = RemoteRepos.getNamedSchedule(
                namedScheduleId = 0,
                name = name,
                apiId = apiId,
                type = type
            )) {
                is Result.Error -> {
                    _stateSchedule.value = ScheduleState.Error
                }

                is Result.Success -> {
                    loadSchedule(
                        namedSchedule = namedSchedule.data,
                        isSaved = false
                    )
                }
            }
        }
    }

    fun saveSchedule() {
        viewModelScope.launch {
            val state = _stateSchedule.value as ScheduleState.Loaded
            localRepos.insertNewNamedSchedule(state.namedSchedule)
            val schedules = localRepos.getAll()
            loadSchedules(schedules)
            loadSchedule(
                namedSchedule = schedules.find { it.namedScheduleEntity.apiId == state.namedSchedule.namedScheduleEntity.apiId }
                    ?: schedules.first(),
                isSaved = true
            )
        }

    }

    fun selectNamedSchedule(
        id: Long
    ) {
        viewModelScope.launch {
            localRepos.updatePriorityNamedSchedule(id)
            val newDefaultNamedSchedule = load()
            getUpdatedNamedSchedule(newDefaultNamedSchedule!!)
        }
    }

    fun selectSchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        apiId: String,
        timetableId: String
    ) {
        viewModelScope.launch {
            val scheduleState = _stateSchedule.value as ScheduleState.Loaded
            if (scheduleState.isSaved) {
                localRepos.updatePrioritySchedule(primaryKeySchedule, primaryKeyNamedSchedule)
                val updatedNamedSchedule = localRepos.getNamedScheduleByApiId(apiId)
                loadSchedule(
                    namedSchedule = updatedNamedSchedule!!,
                    isSaved = true
                )
            } else {
                val updatedSchedules =
                    scheduleState.namedSchedule.schedules.map { scheduleFormatted ->
                        scheduleFormatted.apply {
                            scheduleEntity.isDefault = scheduleEntity.timetableId == timetableId
                        }
                    }
                loadSchedule(
                    namedSchedule = scheduleState.namedSchedule
                        .copy(schedules = updatedSchedules),
                    isSaved = false
                )

            }
        }
    }

    fun updateEventExtra(
        apiId: String,
        scheduleId: Long,
        event: Event,
        comment: String,
        tag: Int
    ) {
        viewModelScope.launch {
            val scheduleState = _stateSchedule.value as ScheduleState.Loaded
            localRepos.updateEventExtra(
                scheduleId = scheduleId,
                event = event,
                comment = comment,
                tag = tag
            )
            val updatedSchedule = localRepos.getNamedScheduleByApiId(apiId)
            _stateSchedule.value = scheduleState.copy(
                namedSchedule = updatedSchedule!!,
                selectedSchedule = updatedSchedule.schedules.first { it.scheduleEntity.id == scheduleId }
            )
        }
    }
}