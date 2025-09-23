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
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
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

sealed interface UiEvent {
    data class ShowErrorMessage(val message: String) : UiEvent
    data class ShowInfoMessage(val message: String) : UiEvent
}

data class ScheduleUiState(
    val savedNamedSchedules: List<NamedScheduleEntity> = emptyList(),
    val currentNamedSchedule: NamedScheduleFormatted? = null,
    val currentScheduleEntity: ScheduleEntity? = null,
    val currentScheduleData: ScheduleData? = null,
    val isUpdating: Boolean = false,
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

interface ScheduleViewModel {
    val uiState: StateFlow<ScheduleUiState>
    val uiEvent: Flow<UiEvent>

    fun loadInitialData(showLoading: Boolean = true)
    fun getNamedScheduleFromApi(name: String, apiId: String, type: Int)
    fun getNamedScheduleFromDb(primaryKeyNamedSchedule: Long, setDefault: Boolean = false)
    fun saveCurrentNamedSchedule()
    fun deleteNamedSchedule(primaryKeyNamedSchedule: Long, isDefault: Boolean)
    fun setNewDefaultSchedule(primaryKeySchedule: Long, timetableId: String)
    fun updateEventExtra(event: Event, comment: String, tag: Int)
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
            updateUiStateParams(
                isLoading = showLoading
            )
            val savedNamedSchedules = repos.getAllNamedSchedules()
            val defaultNamedScheduleEntity = savedNamedSchedules.find { it.isDefault }
                ?: savedNamedSchedules.firstOrNull()

            if (defaultNamedScheduleEntity != null) {
                val defaultNamedSchedule = repos.getNamedScheduleById(defaultNamedScheduleEntity.id)
                updateUiStateNamedSchedule(
                    namedSchedule = defaultNamedSchedule
                )
            }

            updateUiStateParams(
                savedNamedSchedules = savedNamedSchedules,
                isLoading = false,
                isSaved = defaultNamedScheduleEntity != null,
            )

            defaultNamedScheduleEntity?.let { namedScheduleEntity ->
                updateNamedSchedule(namedScheduleEntity)
                updateUiStateNamedSchedule(
                    namedSchedule = repos.getNamedScheduleByApiId(namedScheduleEntity.apiId!!)
                )
            }
        }
    }

    private suspend fun updateUiStateParams (
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
                isSaved = isSaved ?: it.isSaved,
                isSavingAvailable = repos.isSavingAvailable()
            )
        }
    }

    private fun updateUiStateNamedSchedule(
        namedSchedule: NamedScheduleFormatted?,
        scheduleId: Long? = null
    ) {
        val currentSchedule = findCurrentSchedule(namedSchedule, scheduleId)
        _uiState.update {
            it.copy(
                currentNamedSchedule = namedSchedule,
                currentScheduleEntity = currentSchedule?.scheduleEntity,
                currentScheduleData = calculateScheduleData(currentSchedule),
            )
        }
    }

    private suspend fun sendErrorUiEvent (
        message: String
    ) {
        _uiEventChannel.send(UiEvent.ShowErrorMessage(message))
    }

    private fun findCurrentSchedule(
        namedSchedule: NamedScheduleFormatted?,
        scheduleId: Long?
    ): ScheduleFormatted? {
        if (namedSchedule == null) return null
        if (scheduleId == null) return findDefaultSchedule(namedSchedule)
        return namedSchedule.schedules.firstOrNull { s -> s.scheduleEntity.id == scheduleId }
    }

    private fun findDefaultSchedule(
        namedSchedule: NamedScheduleFormatted
    ): ScheduleFormatted? {
        return namedSchedule.schedules.find { it.scheduleEntity.isDefault }
            ?: namedSchedule.schedules.firstOrNull()
    }


    override fun getNamedScheduleFromApi(
        name: String,
        apiId: String,
        type: Int
    ) {
        val fetchJob = viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()
            updateUiStateParams(isLoading = true)

            val localNamedSchedule = repos.getNamedScheduleByApiId(apiId)
            if (localNamedSchedule != null) {
                updateUiStateNamedSchedule(
                    namedSchedule = localNamedSchedule
                )
                updateUiStateParams(
                    isError = false,
                    isLoading = false,
                    isSaved = true
                )
                updateNamedSchedule(localNamedSchedule.namedScheduleEntity)
                updateUiStateNamedSchedule(
                    namedSchedule = repos.getNamedScheduleByApiId(localNamedSchedule.namedScheduleEntity.apiId!!)
                )
                return@launch
            }

            when (val newNamedSchedule = repos.getNamedSchedule(name = name, apiId = apiId, type = type)) {
                is Result.Success -> {
                    updateUiStateNamedSchedule(
                        namedSchedule = newNamedSchedule.data
                    )
                    updateUiStateParams(
                        isError = false,
                        isLoading = false,
                        isSaved = false
                    )
                }

                is Result.Error -> {
                    updateUiStateParams(
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

            updateUiStateNamedSchedule(
                namedSchedule = namedSchedule
            )
            if (namedSchedule != null) {
                updateUiStateNamedSchedule(
                    namedSchedule = repos.getNamedScheduleByApiId(namedSchedule.namedScheduleEntity.apiId!!)
                )
                updateUiStateParams(
                    isSaved = true
                )
                updateNamedSchedule(namedSchedule.namedScheduleEntity)
            }
        }
    }

    override fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            val currentNamedSchedule = _uiState.value.currentNamedSchedule ?: return@launch
            if (_uiState.value.isSaved) return@launch

            repos.insertNamedSchedule(currentNamedSchedule)

            updateUiStateNamedSchedule(
                namedSchedule = repos.getNamedScheduleByApiId(currentNamedSchedule.namedScheduleEntity.apiId!!)
            )
            updateUiStateParams(
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

            updateUiStateParams(
                savedNamedSchedules = repos.getAllNamedSchedules(),
                isSaved = (primaryKeyNamedSchedule != _uiState.value.currentNamedSchedule!!.namedScheduleEntity.id) && _uiState.value.isSaved
            )
        }
    }

    override fun setNewDefaultSchedule(
        primaryKeySchedule: Long,
        timetableId: String
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentNamedSchedule = currentState.currentNamedSchedule ?: return@launch

            if (currentState.isSaved) {
                repos.updatePrioritySchedule(
                    primaryKeySchedule,
                    currentNamedSchedule.namedScheduleEntity.id
                )

                updateUiStateNamedSchedule(
                    namedSchedule =  repos.getNamedScheduleById(currentNamedSchedule.namedScheduleEntity.id)
                )
            } else {
                val updatedSchedules = currentNamedSchedule.schedules.map { schedule ->
                    schedule.copy(
                        scheduleEntity = schedule.scheduleEntity.copy(
                            isDefault = schedule.scheduleEntity.timetableId == timetableId
                        )
                    )
                }
                updateUiStateNamedSchedule(
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
                _uiState.value.currentNamedSchedule?.namedScheduleEntity?.id ?: return@launch
            val scheduleId = _uiState.value.currentScheduleEntity?.id ?: return@launch

            repos.updateEventExtra(
                scheduleId = scheduleId,
                event = event,
                comment = comment,
                tag = tag
            )

            updateUiStateNamedSchedule(
                namedSchedule = repos.getNamedScheduleById(namedScheduleId),
                scheduleId = scheduleId
            )
        }
    }

    private suspend fun updateNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity
    ) {
        updateUiStateParams(
            isUpdating = true
        )
        when (val resultUpdate = repos.updateNamedSchedule(namedScheduleEntity = namedScheduleEntity)) {
            is Result.Success -> {
                updateUiStateParams(
                    savedNamedSchedules = repos.getAllNamedSchedules()
                )
            }
            is Result.Error -> {
                val errorMessage = resultUpdate.exception.message
                    ?: resourcesManager.getString(R.string.unknown_error)
                sendErrorUiEvent(
                    message = "${resourcesManager.getString(R.string.failed_update)}: $errorMessage"
                )
            }
        }
        updateUiStateParams(
            isUpdating = false
        )
    }



    private fun calculateScheduleData(
        scheduleFormatted: ScheduleFormatted?
    ): ScheduleData? {
        return if (scheduleFormatted != null && scheduleFormatted.events.isNotEmpty()) {
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
                eventsForCalendar = eventsForCalendar,
                scheduleEntity = scheduleFormatted.scheduleEntity,
            )

            ScheduleData(
                weeksCount = weeksCount,
                defaultDate = defaultParams.first,
                weeksStartIndex = defaultParams.second,
                daysStartIndex = defaultParams.third,
                eventsForCalendar = eventsForCalendar,
                eventsExtraData = scheduleFormatted.eventsExtraData,
                eventForList = eventsForList
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
        currentSchedule: ScheduleFormatted
    ): Map<Int, Map<LocalDate, List<Event>>> {
        val recurrence = currentSchedule.scheduleEntity.recurrence
            ?: return mapOf(
                1 to currentSchedule.events
                    .groupBy { it.startDatetime!!.toLocalDate() })

        return buildMap {
            for (week in 1..recurrence.interval!!) {
                val eventsForWeek = currentSchedule.events.filter { event ->
                    val rule = event.recurrenceRule ?: return@filter false
                    rule.interval == 1 || event.periodNumber == week
                }
                if (eventsForWeek.isNotEmpty()) {
                    this[week] = eventsForWeek.groupBy { it.startDatetime!!.toLocalDate() }
                }
            }
        }
    }

    private fun calculateEventsForList(
        eventsForCalendar: Map<Int, Map<LocalDate, List<Event>>>,
        today: LocalDate,
        scheduleEntity: ScheduleEntity
    ): List<Pair<LocalDate, List<Event>>> {
        if (scheduleEntity.recurrence == null) {
            return eventsForCalendar[1]?.values
                .orEmpty()
                .asSequence()
                .flatten()
                .filter { it.startDatetime?.toLocalDate()?.isAfter(today.minusDays(1)) == true }
                .sortedBy { it.startDatetime }
                .groupBy { it.startDatetime!!.toLocalDate() }
                .toList()
        }
        val startDate = calculateFirstDayOfWeek(maxOf(today, scheduleEntity.startDate))
        val allEvents = buildList {
            val weeksNumbers = getWeeksNumbers(startDate, scheduleEntity)
            weeksNumbers.forEachIndexed { index, week ->
                val eventsInWeek = eventsForCalendar[week]?.values.orEmpty().flatten()
                val currentWeekStartDate = startDate.plusWeeks(index.toLong())
                eventsInWeek.forEach { event ->
                    val eventStartDayOfWeek =
                        event.startDatetime?.toLocalDate()?.dayOfWeek?.value ?: return@forEach
                    val newEventDate = currentWeekStartDate.plusDays(eventStartDayOfWeek - 1L)
                    if (newEventDate.isAfter(scheduleEntity.endDate)) return@forEach
                    val newEvent = event.copy(
                        startDatetime = newEventDate.atTime(event.startDatetime.toLocalTime()),
                        endDatetime = newEventDate.atTime(event.endDatetime?.toLocalTime()),
                    )
                    add(newEvent)
                }
            }
        }

        return allEvents
            .filter { it.startDatetime?.toLocalDate()?.isAfter(today.minusDays(1)) == true }
            .sortedBy { it.startDatetime }
            .groupBy { it.startDatetime!!.toLocalDate() }
            .toList()
    }

    private fun getWeeksNumbers(
        startDate: LocalDate,
        scheduleEntity: ScheduleEntity
    ): List<Int> {
        val weeksCount = ChronoUnit.WEEKS.between(
            calculateFirstDayOfWeek(scheduleEntity.startDate),
            calculateFirstDayOfWeek(scheduleEntity.endDate)
        ).toInt() + 1
        val weeksRemaining =
            ChronoUnit.WEEKS.between(startDate, calculateFirstDayOfWeek(scheduleEntity.endDate))
                .toInt() + 1
        val recurrence = scheduleEntity.recurrence ?: return emptyList()

        return (1..weeksCount)
            .asSequence()
            .map { week -> ((week + recurrence.firstWeekNumber) % recurrence.interval!!).plus(1) }
            .toList()
            .drop((weeksCount - weeksRemaining))
    }
}