package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScreenState
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.ui.event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.event.sendErrorEvent
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.findDefault
import com.egormelnikoff.schedulerutmiit.schedule.domain.manager.ScheduleManager
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.FetchNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.SetDefaultScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.state.ScheduleState.Companion.withExtraData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
    private val fetchNamedScheduleUseCase: FetchNamedScheduleUseCase,
    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,
    private val setDefaultScheduleUseCase: SetDefaultScheduleUseCase,
    private val eventActionUseCase: EventActionUseCase,
    private val scheduleManager: ScheduleManager
) : ViewModel() {
    private var previousState: NamedScheduleState = NamedScheduleState.Loading

    private val _screenState = MutableStateFlow(ScreenState())
    private val _uiEventFlow = MutableSharedFlow<UiEvent>()

    private var fetchScheduleJob: Job? = null
    private var updateScheduleJob: Job? = null

    val screenState = _screenState.asStateFlow()
    val uiEvent = _uiEventFlow.asSharedFlow()


    val namedScheduleState = scheduleManager.currentNamedSchedule
        .map { current ->
            val state = handleNamedSchedule(
                oldState = previousState,
                newNamedSchedule = current
            ).second

            updateScreenState(
                isSaved = !scheduleManager.isTemp
            )

            previousState = state

            state
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NamedScheduleState.Loading
        )


    fun cancelLoading() {
        viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()
            updateScreenState(
                isLoading = false,
                isRefreshing = false,
                isError = false
            )
        }
    }

    fun cancelRefresh() {
        viewModelScope.launch {
            updateScheduleJob?.cancelAndJoin()
            updateScreenState(
                isLoading = false,
                isRefreshing = false,
                isError = false
            )
        }
    }

    fun refreshScheduleState(
        namedScheduleId: Long? = null,
        updating: Boolean = false,
        showLoading: Boolean = true
    ) {
        val newUpdateScheduleJob = viewModelScope.launch {
            updateScheduleJob?.cancelAndJoin()
            updateScreenState(
                isLoading = showLoading,
                isRefreshing = updating,
                isError = false
            )
            refreshNamedScheduleUseCase(
                namedScheduleId, updating
            )

            if (namedScheduleId != null) scheduleManager.openSaved(namedScheduleId)
            else scheduleManager.openDefault()

            updateScreenState(
                isLoading = false,
                isRefreshing = false
            )
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

            updateScreenState(
                isLoading = true
            )
            val result = fetchNamedScheduleUseCase(
                name = name,
                apiId = apiId,
                namedScheduleType = type
            )

            when (val newNamedSchedule = result.namedScheduleWithSchedules) {
                is Result.Success -> {
                    when {
                        (result.isSaved && result.namedScheduleWithSchedules.data.namedSchedule.isDefault) -> scheduleManager.openDefault()
                        result.isSaved -> scheduleManager.openSaved(result.namedScheduleWithSchedules.data.namedSchedule.id)
                        else -> scheduleManager.openTemp(result.namedScheduleWithSchedules.data)
                    }

                    updateScreenState(
                        isError = false,
                        isLoading = false
                    )

                }

                is Result.Error -> {
                    updateScreenState(
                        isError = true,
                        isLoading = false
                    )

                    _uiEventFlow.sendErrorEvent(newNamedSchedule.typedError)
                }
            }
        }
        fetchScheduleJob = fetchJob
    }

    fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            scheduleManager.saveTemp()
        }
    }

    fun deleteNamedSchedule(
        namedScheduleId: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            scheduleManager.openDefault()

            deleteNamedScheduleUseCase(
                namedScheduleId, isDefault
            )
        }
    }

    fun setDefaultSchedule(
        scheduleId: Long,
        timetableId: String
    ) {
        viewModelScope.launch {
            setDefaultScheduleUseCase(
                currentNamedScheduleWithSchedules = currentNamedSchedule(),
                scheduleId = scheduleId,
                isSaved = _screenState.value.isSaved,
                timetableId = timetableId
            )?.let { result ->
                scheduleManager.openTemp(result)
            }
        }
    }

    fun eventAction(
        eventAction: EventAction
    ) {
        viewModelScope.launch {
            eventActionUseCase(eventAction)
        }
    }

    private fun currentNamedSchedule(): NamedScheduleWithSchedules {
        val state = namedScheduleState.value

        require(state is NamedScheduleState.Loaded)
        return requireNotNull(state.namedScheduleWithSchedules)
    }

    private fun updateScreenState(
        isError: Boolean? = null,
        isRefreshing: Boolean? = null,
        isLoading: Boolean? = null,
        isSaved: Boolean? = null
    ) {
        _screenState.update { state ->
            state.copy(
                isError = isError ?: state.isError,
                isRefreshing = isRefreshing ?: state.isRefreshing,
                isLoading = isLoading ?: state.isLoading,
                isSaved = isSaved ?: state.isSaved
            )
        }
    }

    private fun handleNamedSchedule(
        newNamedSchedule: NamedScheduleWithSchedules?,
        oldState: NamedScheduleState
    ): Pair<NamedScheduleWithSchedules?, NamedScheduleState> {
        if (newNamedSchedule == null) {
            return null to NamedScheduleState.Empty
        }

        val state = oldState as? NamedScheduleState.Loaded
            ?: NamedScheduleState.Loaded(newNamedSchedule)

        val oldSchedule = state.namedScheduleWithSchedules.schedulesWithEvents.findDefault()
        val newSchedule = newNamedSchedule.schedulesWithEvents.findDefault()

        val scheduleState = when {
            state.scheduleState == null ->
                newSchedule?.let(ScheduleState::fromSchedule)

            oldSchedule?.events == newSchedule?.events && oldSchedule?.eventsExtraData != newSchedule?.eventsExtraData ->
                state.scheduleState.withExtraData(newSchedule?.eventsExtraData)

            oldSchedule != newSchedule ->
                newSchedule?.let(ScheduleState::fromSchedule)

            else -> state.scheduleState
        }

        return newNamedSchedule to state.copy(
            namedScheduleWithSchedules = newNamedSchedule,
            scheduleState = scheduleState
        )
    }
}