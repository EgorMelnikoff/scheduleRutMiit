package com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.egormelnikoff.schedulerutmiit.AppContainer
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.repos.Repos
import com.egormelnikoff.schedulerutmiit.ui.schedule.calculateFirstDayOfWeek
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
import java.time.temporal.ChronoUnit
import kotlin.math.abs

interface ScheduleViewModel {
    val uiState: StateFlow<ScheduleUiState>
    val uiEvent: Flow<UiEvent>
    fun loadInitialData(showIsLoading: Boolean)
    fun getAndSetNamedSchedule(name: String, apiId: String, type: Int)
    fun selectDefaultNamedSchedule(primaryKey: Long)
    fun selectDefaultSchedule(primaryKeySchedule: Long, timetableId: String?)
    fun saveCurrentNamedSchedule()
    fun deleteNamedSchedule(primaryKey: Long, isDefault: Boolean)
    fun updateEventExtra(event: Event, comment: String, tag: Int)
}

data class ScheduleUiState(
    val savedNamedSchedules: List<NamedScheduleFormatted> = emptyList(),
    val currentNamedSchedule: NamedScheduleFormatted? = null,
    val currentScheduleEntity: ScheduleEntity? = null,
    val currentScheduleData: ScheduleData? = null,
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isSavingAvailable: Boolean = false
)

data class ScheduleData(
    val defaultDate: LocalDate,
    val daysStartIndex: Int,
    val weeksStartIndex: Int,
    val weeksCount: Int,
    val eventsForCalendar: Map<Int, Map<LocalDate, List<Event>>> = mapOf(),
    val eventForList: List<Pair<LocalDate, List<Event>>> = listOf(),
    val eventsExtraData: List<EventExtraData> = listOf()
)


sealed interface UiEvent {
    data class ShowErrorMessage(val message: String) : UiEvent
    data class ShowInfoMessage(val message: String) : UiEvent
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
        loadInitialData(true)
    }

    override fun loadInitialData(showIsLoading: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = showIsLoading) }
            val savedNamedSchedules = repos.getAllNamedSchedules()

            val defaultNamedSchedule = savedNamedSchedules.find { it.namedScheduleEntity.isDefault }
                ?: savedNamedSchedules.firstOrNull()

            val currentSchedule = findDefaultSchedule(defaultNamedSchedule)
            _uiState.update {
                it.copy(
                    savedNamedSchedules = savedNamedSchedules,
                    currentNamedSchedule = defaultNamedSchedule,
                    currentScheduleEntity = currentSchedule?.scheduleEntity,
                    currentScheduleData = calculateScheduleData(currentSchedule),
                    isLoading = false,
                    isSaved = defaultNamedSchedule != null,
                    isSavingAvailable = repos.isSavingAvailable()
                )
            }

            defaultNamedSchedule?.let { namedSchedule ->
                val resultUpdate = repos.updateNamedSchedule(
                    namedSchedule = namedSchedule,
                    onStartUpdate = {
                        viewModelScope.launch {
                            _uiEventChannel.send(UiEvent.ShowInfoMessage("${resourcesManager.getString(R.string.updating)}..."))
                        }
                    }
                )

                if (resultUpdate is Result.Error) {
                    val errorMsg = resultUpdate.exception.message ?: resourcesManager.getString(R.string.unknown_error)
                    _uiEventChannel.send(UiEvent.ShowErrorMessage("${resourcesManager.getString(R.string.failed_update)}: $errorMsg"))

                }
            }
        }
    }

    override fun getAndSetNamedSchedule(name: String, apiId: String, type: Int) {
        val fetchJob = viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()
            _uiState.update { it.copy(isLoading = true) }

            val localNamedSchedule = repos.getNamedScheduleByApiId(apiId)
            if (localNamedSchedule != null) {
                val defaultSchedule = findDefaultSchedule(localNamedSchedule)
                _uiState.update {
                    it.copy(
                        isError = false,
                        isLoading = false,
                        currentNamedSchedule = localNamedSchedule,
                        currentScheduleEntity = defaultSchedule?.scheduleEntity,
                        isSaved = true
                    )
                }
                val resultUpdate = repos.updateNamedSchedule(
                    namedSchedule = localNamedSchedule,
                    onStartUpdate = {
                        viewModelScope.launch {
                            _uiEventChannel.send(UiEvent.ShowInfoMessage("${resourcesManager.getString(R.string.updating)}..."))
                        }
                    }
                )

                if (resultUpdate is Result.Error) {
                    val errorMsg = resultUpdate.exception.message ?: resourcesManager.getString(R.string.unknown_error)
                    _uiEventChannel.send(UiEvent.ShowErrorMessage("${resourcesManager.getString(R.string.failed_update)}: $errorMsg"))
                }
                return@launch
            }

            when (val result =
                repos.getNamedSchedule(name = name, apiId = apiId, type = type)) {
                is Result.Success -> {
                    val newNamedSchedule = result.data
                    val defaultSchedule = findDefaultSchedule(newNamedSchedule)
                    _uiState.update {
                        it.copy(
                            isError = false,
                            isLoading = false,
                            currentNamedSchedule = newNamedSchedule,
                            currentScheduleEntity = defaultSchedule?.scheduleEntity,
                            currentScheduleData = calculateScheduleData(defaultSchedule),
                            isSaved = false
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isError = true,
                            isLoading = false
                        )
                    }
                    _uiEventChannel.send(UiEvent.ShowErrorMessage("${result.exception.message}")) //Loading error
                }
            }
        }
        fetchScheduleJob = fetchJob
    }

    override fun selectDefaultNamedSchedule(primaryKey: Long) {
        viewModelScope.launch {
            repos.updatePriorityNamedSchedule(primaryKey)
            loadInitialData(false)
        }
    }

    override fun selectDefaultSchedule(primaryKeySchedule: Long, timetableId: String?) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentNamedSchedule = currentState.currentNamedSchedule ?: return@launch

            if (currentState.isSaved) {
                repos.updatePrioritySchedule(
                    primaryKeySchedule,
                    currentNamedSchedule.namedScheduleEntity.id
                )
                val updatedNamedSchedule =
                    repos.getNamedScheduleById(currentNamedSchedule.namedScheduleEntity.id)

                val schedule =
                    updatedNamedSchedule?.schedules?.find { s -> s.scheduleEntity.isDefault }

                _uiState.update {
                    it.copy(
                        currentNamedSchedule = updatedNamedSchedule,
                        currentScheduleEntity = schedule?.scheduleEntity,
                        currentScheduleData = calculateScheduleData(schedule)
                    )
                }
            } else {
                val updatedSchedules = currentNamedSchedule.schedules.map { schedule ->
                    schedule.copy(
                        scheduleEntity = schedule.scheduleEntity.copy(
                            isDefault = schedule.scheduleEntity.timetableId == timetableId
                        )
                    )
                }
                val updatedNamedSchedule = currentNamedSchedule.copy(schedules = updatedSchedules)
                val updatedDefaultSchedule = findDefaultSchedule(updatedNamedSchedule)
                _uiState.update {
                    it.copy(
                        currentNamedSchedule = updatedNamedSchedule,
                        currentScheduleEntity = updatedDefaultSchedule?.scheduleEntity,
                        currentScheduleData = calculateScheduleData(updatedDefaultSchedule)
                    )
                }
            }
        }
    }

    override fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            val currentNamedSchedule = _uiState.value.currentNamedSchedule ?: return@launch
            if (_uiState.value.isSaved) return@launch

            repos.insertNamedSchedule(currentNamedSchedule)
            val updatedCurrentNamedSchedule =
                repos.getNamedScheduleByApiId(currentNamedSchedule.namedScheduleEntity.apiId!!)
            val updatesCurrentSchedule =
                updatedCurrentNamedSchedule!!.schedules.find { it.scheduleEntity.isDefault }
                    ?: updatedCurrentNamedSchedule.schedules.firstOrNull()
            _uiState.update { value ->
                value.copy(
                    savedNamedSchedules = repos.getAllNamedSchedules(),
                    currentNamedSchedule = updatedCurrentNamedSchedule,
                    currentScheduleEntity = updatesCurrentSchedule?.scheduleEntity,
                    currentScheduleData = calculateScheduleData(updatesCurrentSchedule),
                    isSaved = true,
                    isSavingAvailable = repos.isSavingAvailable()
                )
            }
        }
    }

    override fun deleteNamedSchedule(primaryKey: Long, isDefault: Boolean) {
        viewModelScope.launch {
            repos.deleteNamedSchedule(primaryKey, isDefault)

            _uiState.update {
                it.copy(
                    savedNamedSchedules = repos.getAllNamedSchedules(),
                    isSaved = (primaryKey != _uiState.value.currentNamedSchedule!!.namedScheduleEntity.id) && _uiState.value.isSaved,
                    isSavingAvailable = repos.isSavingAvailable()
                )
            }
        }
    }

    override fun updateEventExtra(event: Event, comment: String, tag: Int) {
        viewModelScope.launch {
            val scheduleId = _uiState.value.currentScheduleEntity?.id ?: return@launch
            val namedScheduleId =
                _uiState.value.currentNamedSchedule?.namedScheduleEntity?.id ?: return@launch

            repos.updateEventExtra(
                scheduleId = scheduleId,
                event = event,
                comment = comment,
                tag = tag
            )

            val updatedNamedSchedule = repos.getNamedScheduleById(namedScheduleId)
            val updatedCurrentSchedule =
                updatedNamedSchedule?.schedules?.firstOrNull { s -> s.scheduleEntity.id == scheduleId }
            _uiState.update {
                it.copy(
                    currentNamedSchedule = updatedNamedSchedule,
                    currentScheduleEntity = updatedCurrentSchedule?.scheduleEntity,
                    currentScheduleData = calculateScheduleData(updatedCurrentSchedule)
                )
            }
        }
    }

    private fun findDefaultSchedule(namedSchedule: NamedScheduleFormatted?): ScheduleFormatted? {
        if (namedSchedule == null) return null
        return namedSchedule.schedules.find { it.scheduleEntity.isDefault }
            ?: namedSchedule.schedules.firstOrNull()
    }

    private fun calculateScheduleData(
        scheduleFormatted: ScheduleFormatted?
    ): ScheduleData? {
        return if (scheduleFormatted != null) {
            val today = LocalDate.now()
            val weeksCount = ChronoUnit.WEEKS.between(
                calculateFirstDayOfWeek(scheduleFormatted.scheduleEntity.startDate),
                calculateFirstDayOfWeek(scheduleFormatted.scheduleEntity.endDate)
            ).plus(1).toInt()

            val defaultParams = calculateDefaultParams(
                today = today,
                weeksCount = weeksCount,
                scheduleEntity = scheduleFormatted.scheduleEntity
            )

            val eventsForCalendar = calculateEventsForCalendar(scheduleFormatted)
            val eventsForList = calculateEventsForList(
                today = today,
                scheduleEntity = scheduleFormatted.scheduleEntity,
                eventsByWeekAndDays = eventsForCalendar
            )
            ScheduleData(
                weeksCount = weeksCount,
                defaultDate = defaultParams.first,
                daysStartIndex = defaultParams.second,
                weeksStartIndex = defaultParams.third,
                eventsForCalendar = eventsForCalendar,
                eventForList = eventsForList,
                eventsExtraData = scheduleFormatted.eventsExtraData
            )
        } else null
    }

    private fun calculateDefaultParams(
        today: LocalDate,
        weeksCount: Int,
        scheduleEntity: ScheduleEntity
    ): Triple<LocalDate, Int, Int> {
        val defaultDate: LocalDate
        val weeksStartIndex: Int
        val daysStartIndex: Int

        if (today in scheduleEntity.startDate..scheduleEntity.endDate) {
            weeksStartIndex = abs(
                ChronoUnit.WEEKS.between(
                    calculateFirstDayOfWeek(scheduleEntity.startDate),
                    calculateFirstDayOfWeek(today)
                ).toInt()
            )
            daysStartIndex = abs(
                ChronoUnit.DAYS.between(
                    scheduleEntity.startDate,
                    today
                ).toInt()
            )
            defaultDate = today
        } else if (today < scheduleEntity.startDate) {
            weeksStartIndex = 0
            daysStartIndex = 0
            defaultDate = scheduleEntity.startDate
        } else {
            weeksStartIndex = weeksCount
            daysStartIndex = weeksCount * 7
            defaultDate = scheduleEntity.endDate
        }
        return Triple(defaultDate, weeksStartIndex, daysStartIndex)
    }


    private fun calculateEventsForCalendar(
        currentSchedule: ScheduleFormatted?
    ): Map<Int, Map<LocalDate, List<Event>>> {
        val eventsByWeekAndDay = mutableMapOf<Int, Map<LocalDate, List<Event>>>()
        if (currentSchedule != null) {
            if (currentSchedule.scheduleEntity.recurrence != null) {
                for (week in 1..currentSchedule.scheduleEntity.recurrence.interval!!) {
                    val eventsForWeek =
                        currentSchedule.events.filter { event ->
                            event.recurrenceRule?.let { rule ->
                                rule.interval == 1 || (rule.interval > 1 && week == event.periodNumber)
                            } ?: false
                        }


                    eventsByWeekAndDay[week] =
                        eventsForWeek.groupBy { it.startDatetime!!.toLocalDate() }

                }
            } else {
                eventsByWeekAndDay[1] =
                    currentSchedule.events.groupBy { it.startDatetime!!.toLocalDate() }

            }
        }
        return eventsByWeekAndDay

    }

    private fun calculateEventsForList(
        eventsByWeekAndDays: Map<Int, Map<LocalDate, List<Event>>>,
        today: LocalDate,
        scheduleEntity: ScheduleEntity
    ): List<Pair<LocalDate, List<Event>>> {
        val displayedEvents = mutableListOf<Event>()
        if (scheduleEntity.recurrence != null) {
            val startDate = if (today < scheduleEntity.startDate) {
                scheduleEntity.startDate
            } else {
                today
            }
            val weeksRemaining = abs(
                ChronoUnit.WEEKS.between(
                    calculateFirstDayOfWeek(startDate),
                    calculateFirstDayOfWeek(scheduleEntity.endDate)
                )
            ).toInt().plus(1)
            for (week in 1..weeksRemaining) {
                val weekNumber =
                    ((week + scheduleEntity.recurrence.firstWeekNumber) % scheduleEntity.recurrence.interval!!).plus(
                        1
                    )
                val events = eventsByWeekAndDays[weekNumber]?.values?.flatten()
                if (events != null) {
                    for (event in events) {
                        val startOfWeek = calculateFirstDayOfWeek(startDate)
                            .plusDays(event.startDatetime!!.toLocalDate().dayOfWeek.value - 1L)
                            .plusWeeks(week - 1L)

                        val newEvent = event.copy(
                            startDatetime = startOfWeek.atTime(event.startDatetime.toLocalTime()),
                            endDatetime = startOfWeek.atTime(event.endDatetime?.toLocalTime())
                        )

                        displayedEvents.add(newEvent)
                    }
                }
            }
        } else {
            if (!eventsByWeekAndDays[1]?.values.isNullOrEmpty()) {
                displayedEvents.addAll(eventsByWeekAndDays[1]?.values!!.flatten())
            }
        }
        return displayedEvents.filter { it.startDatetime!!.toLocalDate() >= today }
            .sortedBy { it.startDatetime }
            .groupBy { event ->
                event.startDatetime!!.toLocalDate()
            }
            .toList()
    }
}