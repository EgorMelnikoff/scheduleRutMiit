package com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.egormelnikoff.schedulerutmiit.AppContainerInterface
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.repos.local.LocalReposInterface
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteReposInterface
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
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

class ScheduleViewModel(
    private val localRepos: LocalReposInterface,
    private val remoteRepos: RemoteReposInterface
) : ViewModel() {
    companion object {
        fun provideFactory(container: AppContainerInterface): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    return ScheduleViewModel(
                        localRepos = container.localRepos,
                        remoteRepos = container.remoteRepos
                    ) as T
                }
            }
        }
    }

    private var scheduleJob: Job? = null

    private val _stateSchedules = MutableStateFlow<SchedulesState>(SchedulesState.EmptyBase)
    val stateSchedules: StateFlow<SchedulesState> = _stateSchedules

    private val _stateSchedule = MutableStateFlow<ScheduleState>(ScheduleState.EmptyBase)
    val stateSchedule: StateFlow<ScheduleState> = _stateSchedule

    init {
        viewModelScope.launch {
            val defaultNamedSchedule = loadNamedSchedulesFromDb()
            if (defaultNamedSchedule != null) {
                updateAndSetNamedSchedule(defaultNamedSchedule)
            }
        }
    }


    suspend fun loadNamedSchedulesFromDb(): NamedScheduleFormatted? {
        val namedSchedules = localRepos.getAllNamedSchedules()
        return if (namedSchedules.isEmpty()) {
            _stateSchedule.value = ScheduleState.EmptyBase
            null
        } else {
            val defaultNamedSchedule = namedSchedules.find { it.namedScheduleEntity.isDefault }
                ?: namedSchedules.first()
            setNamedSchedules(namedSchedules)
            setNamedSchedule(
                namedSchedule = defaultNamedSchedule,
                isSaved = true
            )
            defaultNamedSchedule
        }
    }

    private fun setNamedSchedules(
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

    private suspend fun setNamedSchedule(
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

    fun getAndSetNamedSchedule(
        name: String,
        apiId: String,
        type: Int
    ) {
        _stateSchedule.value = ScheduleState.Loading
        val getNamedScheduleJob = viewModelScope.launch {
            scheduleJob?.cancelAndJoin()
            val checkedNamedSchedule = localRepos.getNamedScheduleByApiId(apiId)
            if (checkedNamedSchedule != null) {
                setNamedSchedule(
                    namedSchedule = checkedNamedSchedule,
                    isSaved = true
                )
                return@launch
            }
            when (val namedSchedule = remoteRepos.getNamedSchedule(
                namedScheduleId = 0,
                name = name,
                apiId = apiId,
                type = type
            )) {
                is Result.Error -> {
                    _stateSchedule.value = ScheduleState.Error
                }

                is Result.Success -> {
                    setNamedSchedule(
                        namedSchedule = namedSchedule.data,
                        isSaved = false
                    )
                }
            }
        }
        scheduleJob = getNamedScheduleJob
    }

    private suspend fun updateAndSetNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ) {
        if (System.currentTimeMillis() - namedSchedule.namedScheduleEntity.lastTimeUpdate > 43200000
            && namedSchedule.namedScheduleEntity.type != 3
        ) {
            val updatedNamedSchedule = remoteRepos.getNamedSchedule(
                namedScheduleId = namedSchedule.namedScheduleEntity.id,
                name = namedSchedule.namedScheduleEntity.shortName,
                apiId = namedSchedule.namedScheduleEntity.apiId!!,
                type = namedSchedule.namedScheduleEntity.type
            )

            when (updatedNamedSchedule) {
                is Result.Error -> {
                    _stateSchedule.value = (stateSchedule.value as ScheduleState.Loaded).copy(
                        message = if (updatedNamedSchedule.exception.message != null) {
                            //currentContext.getString(R.string.failed_update_schedules)+
                            "\n${updatedNamedSchedule.exception.message}"
                        } else {
                            "failed_update_schedules"
                            //currentContext.getString(R.string.failed_update_schedules)
                        }
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
                            if (ids.sorted() == ids.toSet().sorted()) {
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
                                _stateSchedule.value =
                                    (stateSchedule.value as ScheduleState.Loaded).copy(
                                        message = "failed_update"
                                        //currentContext.getString(R.string.failed_update)
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
                            && LocalDate.now() > oldSchedule.scheduleEntity.endDate
                        ) {
                            localRepos.deleteSchedule(
                                id = oldSchedule.scheduleEntity.id,
                                deleteEventsExtra = true
                            )
                        }
                    }
                    loadNamedSchedulesFromDb()
                }
            }
        }
    }

    fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            val state = _stateSchedule.value as ScheduleState.Loaded
            localRepos.insertNamedSchedule(state.namedSchedule)
            val schedules = localRepos.getAllNamedSchedules()
            setNamedSchedules(schedules)
            setNamedSchedule(
                namedSchedule = schedules.find { it.namedScheduleEntity.apiId == state.namedSchedule.namedScheduleEntity.apiId }
                    ?: schedules.first(),
                isSaved = true
            )
        }

    }

    fun deleteNamedSchedule(
        primaryKey: Long,
        isDefault: Boolean,
    ) {
        viewModelScope.launch {
            localRepos.deleteNamedSchedule(primaryKey, isDefault)
            setNamedSchedules(localRepos.getAllNamedSchedules())

            val state = _stateSchedule.value as ScheduleState.Loaded
            _stateSchedule.value = state.copy(
                isSaved = (primaryKey != state.namedSchedule.namedScheduleEntity.id) && state.isSaved,
                isSavingAvailable = localRepos.getCount() < 5
            )
        }
    }

    fun selectNamedSchedule(
        id: Long
    ) {
        viewModelScope.launch {
            localRepos.updatePriorityNamedSchedule(id)
            val newDefaultNamedSchedule = loadNamedSchedulesFromDb()
            updateAndSetNamedSchedule(newDefaultNamedSchedule!!)
        }
    }

    fun selectSchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        timetableId: String?
    ) {
        viewModelScope.launch {
            val scheduleState = _stateSchedule.value as ScheduleState.Loaded
            if (scheduleState.isSaved) {
                localRepos.updatePrioritySchedule(primaryKeySchedule, primaryKeyNamedSchedule)
                val updatedNamedSchedule = localRepos.getNamedScheduleById(primaryKeyNamedSchedule)
                setNamedSchedule(
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
                setNamedSchedule(
                    namedSchedule = scheduleState.namedSchedule
                        .copy(schedules = updatedSchedules),
                    isSaved = false
                )

            }
        }
    }

    fun updateEventExtra(
        namedScheduleId: Long,
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

            val updatedSchedule = localRepos.getNamedScheduleById(namedScheduleId)
            _stateSchedule.value = scheduleState.copy(
                namedSchedule = updatedSchedule!!,
                selectedSchedule = updatedSchedule.schedules.first { it.scheduleEntity.id == scheduleId }
            )
        }
    }

}