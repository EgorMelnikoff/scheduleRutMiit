package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.error.AppError
import com.egormelnikoff.schedulerutmiit.core.common.error.toAppError
import com.egormelnikoff.schedulerutmiit.core.common.time_provider.TimeProvider
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiText
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.sendErrorEvent
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.sendInfoEvent
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.cache.FetchScheduleCache
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.FetchScheduleEvent
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.EventDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findDefault
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.SetDefaultScheduleUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.export.ExportDataUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.export.ImportDataUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleState.Companion.withExtraData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,

    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,
    private val setDefaultScheduleUseCase: SetDefaultScheduleUseCase,
    private val eventDataSource: EventDataSource,
    private val namedScheduleRepos: NamedScheduleRepos,
    private val settingsRepos: SettingsRepos,
    private val fetchScheduleCache: FetchScheduleCache,
    private val importDataUseCase: ImportDataUseCase,
    private val exportDataUseCase: ExportDataUseCase,
    private val appRouter: AppRouter,
    timeProvider: TimeProvider
) : ViewModel() {
    private var previousState: NamedScheduleState = NamedScheduleState.Loading

    private val _screenState = MutableStateFlow(ScreenState())
    private val _uiEventFlow = MutableSharedFlow<UiEvent>()

    private var fetchScheduleJob: Job? = null
    private var updateScheduleJob: Job? = null

    val screenState = _screenState.asStateFlow()
    val uiEvent = _uiEventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            fetchScheduleCache.scheduleRequests.collect { event ->
                handleFetchEvent(event)
            }
        }
    }

    val namedScheduleState = namedScheduleRepos.currentNamedSchedule
        .map { current ->
            val state = handleNamedSchedule(
                oldState = previousState,
                newNamedSchedule = current
            ).second

            updateScreenState(
                isSaved = !namedScheduleRepos.isSaved
            )

            previousState = state

            state
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NamedScheduleState.Loading
        )

    val appSettings = settingsRepos.observeAppSettings().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val hourlyDateTime: StateFlow<LocalDateTime> = timeProvider.currentHour
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        )

     fun onImportData(uri: Uri?) {
        viewModelScope.launch {
            if (uri == null) {
                _uiEventFlow.sendErrorEvent(AppError.EmptyBodyError)
                return@launch
            }
            try {
                importDataUseCase(uri)
                _uiEventFlow.emit(
                    UiEvent.InfoMessage(
                        UiText.StringResource(R.string.success)
                    )
                )
                _uiEventFlow.sendInfoEvent(
                    UiEvent.InfoMessage(
                        UiText.StringResource(R.string.success)
                    )
                )
            } catch (e: Exception) {
                _uiEventFlow.sendErrorEvent(e.toAppError())
            }
        }
    }

    private fun exportData(uri: Uri) {
        viewModelScope.launch {
            try {
                exportDataUseCase(uri)
                _uiEventFlow.sendInfoEvent(
                    UiEvent.InfoMessage(
                        UiText.StringResource(R.string.success)
                    )
                )
            } catch (e: Exception) {
                _uiEventFlow.sendErrorEvent(e.toAppError())
            }

        }
    }

    fun onOpenDialog(dialog: Route.Dialog) {
        appRouter.openDialog(dialog)
    }

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

            if (namedScheduleId != null) namedScheduleRepos.getAndOpen(namedScheduleId)
            else namedScheduleRepos.openDefault()

            updateScreenState(
                isLoading = false,
                isRefreshing = false
            )
        }
        updateScheduleJob = newUpdateScheduleJob
    }

    private fun handleFetchEvent(
        fetchScheduleEvent: FetchScheduleEvent
    ) {
        // TODO заменить на launchToCoroutine

        val fetchJob = viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()

            updateScreenState(
                isLoading = true
            )

            namedScheduleRepos.getAndOpen(
                name = fetchScheduleEvent.name,
                apiId = fetchScheduleEvent.apiId,
                type = fetchScheduleEvent.type
            )

            updateScreenState(
                isError = false,
                isLoading = false
            )
        }

        fetchScheduleJob = fetchJob

    }

    fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            namedScheduleRepos.saveOpened()
        }
    }

    fun deleteNamedSchedule(
        namedScheduleId: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            namedScheduleRepos.openDefault()

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
                namedScheduleRepos.openTemp(result)
            }
        }
    }

    fun onDeleteEvent(eventId: Long) {
        viewModelScope.launch {
            eventDataSource.deleteById(eventId)
        }
    }

    fun onHideEvent(eventId: Long) {
        viewModelScope.launch {
            eventDataSource.updateIsHidden(eventId, true)
        }
    }

    fun setScheduleView(scheduleView: ScheduleView) {
        viewModelScope.launch {
            settingsRepos.setScheduleView(scheduleView)
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