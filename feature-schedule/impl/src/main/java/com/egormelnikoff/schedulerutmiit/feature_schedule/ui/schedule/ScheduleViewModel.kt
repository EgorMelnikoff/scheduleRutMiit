package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.error.AppError
import com.egormelnikoff.schedulerutmiit.core.common.error.toAppError
import com.egormelnikoff.schedulerutmiit.core.common.time_provider.TimeProvider
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.network.handler.errorHandler
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiText
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.sendErrorEvent
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.sendInfoEvent
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.cache.FetchScheduleCache
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.FetchScheduleEvent
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.EventDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.RefreshNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.SetDefaultScheduleUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.export.ExportDataUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.export.ImportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
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
    private val timeProvider: TimeProvider,
    private val scheduleUiStateConverter: ScheduleUiStateConverter
) : BaseViewModel<ScheduleState, ScheduleUiState, ScheduleAction>(ScheduleState()) {

    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEventFlow.asSharedFlow()

    private var fetchScheduleJob: Job? = null
    private var updateScheduleJob: Job? = null

    init {
        fetchScheduleCache.scheduleRequests
            .onEach { event ->
                handleFetchEvent(event)
            }
            .launchIn(viewModelScope)
    }

    override fun ScheduleState.toUiState(): ScheduleUiState {
        return scheduleUiStateConverter.toUiState(this)
    }

    override fun onScreenInitialized() {
        namedScheduleRepos.observeAll()
            .onEach { schedules ->
                updateState {
                    copy(namedSchedules = schedules)
                }
            }
            .launchIn(viewModelScope)

        namedScheduleRepos.currentNamedSchedule
            .onEach { current ->
                updateState {
                    copy(
                        namedScheduleWithSchedules = current,
                        isSaved = !namedScheduleRepos.isSaved,
                        mode = when {
                            current != null -> ScheduleState.Mode.Content
                            mode is ScheduleState.Mode.Error -> mode
                            else -> ScheduleState.Mode.Empty
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        settingsRepos.observeAppSettings()
            .onEach { appSettings ->
                updateState {
                    copy(
                        eventExtraPolicy = appSettings.eventExtraPolicy,
                        eventView = appSettings.eventView,
                        scheduleView = appSettings.scheduleView,
                        eventsCountView = appSettings.eventsCountView
                    )
                }
            }
            .launchIn(viewModelScope)

        timeProvider.currentHour
            .onEach { dateTime ->
                updateState {
                    copy(time = dateTime)
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ScheduleAction) {
        when (action) {
            ScheduleAction.OnP2R -> onP2R()
            is ScheduleAction.OnViewClicked -> onViewClicked(action.view)

            ScheduleAction.OnTitleClicked -> updateState {
                copy(
                    schedulesSheet = true,
                    scheduleSheet = false,
                    importSheet = false,
                    returnScheduleSheet = false
                )
            }

            ScheduleAction.OnDismissSchedulesSheet -> updateState {
                copy(schedulesSheet = false)
            }

            is ScheduleAction.OnChangeSchedule -> onSetNamedSchedule(action.id)

            ScheduleAction.OnMenuVertClicked -> updateState {
                copy(
                    scheduleSheet = true,
                    schedulesSheet = false,
                    importSheet = false,
                    returnScheduleSheet = false
                )
            }

            ScheduleAction.OnDismissScheduleSheet -> updateState {
                copy(scheduleSheet = false)
            }

            is ScheduleAction.OnSetDefaultSchedule -> onSetDefaultSchedule(
                action.scheduleId,
                action.timetableId
            )

            ScheduleAction.OnSaveScheduleClicked -> saveCurrentNamedSchedule()

            ScheduleAction.OnDeleteNamedSchedule -> deleteNamedSchedule()

            ScheduleAction.OnImportClicked -> updateState {
                copy(
                    importSheet = true,
                    schedulesSheet = false,
                    scheduleSheet = false,
                    returnScheduleSheet = false
                )
            }

            ScheduleAction.OnDismissImportSheet -> updateState {
                copy(importSheet = false)
            }

            is ScheduleAction.OnImportData -> onImportData(action.uri)
            is ScheduleAction.OnExportData -> onExportData(action.uri)

            ScheduleAction.OnReturnToDefaultClicked -> updateState {
                copy(
                    returnScheduleSheet = true,
                    schedulesSheet = false,
                    scheduleSheet = false,
                    importSheet = false
                )
            }

            ScheduleAction.OnDismissReturnToDefault -> updateState {
                copy(returnScheduleSheet = false)
            }

            ScheduleAction.OnReturnToDefault -> {
                updateState {
                    copy(returnScheduleSheet = false)
                }
                refreshScheduleState(showLoading = false)
            }

            is ScheduleAction.OnEventClicked -> onEventClicked(action.id, action.date)
            is ScheduleAction.OnEditEventClicked -> appRouter.openDialog(
                Route.Dialog.EditEventDialog(
                    eventId = action.eventId,
                    scheduleId = action.scheduleId
                )
            )

            is ScheduleAction.OnHideEvent -> onHideEvent(action.id)
            is ScheduleAction.OnDeleteEvent -> onDeleteEvent(action.id)

            ScheduleAction.OnCancelLoading -> cancelLoading()
            ScheduleAction.OnCancelRefresh -> cancelRefresh()

            is ScheduleAction.OnOpenDialog -> appRouter.openDialog(action.dialog)
        }
    }

    private fun onP2R() {
        val current = screenState.namedScheduleWithSchedules ?: return

        refreshScheduleState(
            namedScheduleId = current.namedSchedule.id,
            updating = true,
            showLoading = false
        )
    }

    private fun onViewClicked(view: ScheduleView) {
        viewModelScope.launch {
            settingsRepos.setScheduleView(view.next())
        }
    }

    private fun onEventClicked(eventId: Long, date: LocalDate?) {
        appRouter.openDialog(
            Route.Dialog.EventDialog(
                eventId = eventId,
                date = date
            )
        )
    }

    private fun onImportData(uri: Uri?) {
        viewModelScope.launch {
            if (uri == null) {
                _uiEventFlow.sendErrorEvent(AppError.EmptyBodyError)
                return@launch
            }
            try {
                importDataUseCase(uri)
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

    private fun onExportData(uri: Uri) {
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

    private fun cancelLoading() {
        viewModelScope.launch {
            fetchScheduleJob?.cancelAndJoin()
            updateScheduleJob?.cancelAndJoin()
            updateState {
                copy(mode = dataMode())
            }
        }
    }

    private fun cancelRefresh() {
        viewModelScope.launch {
            updateScheduleJob?.cancelAndJoin()
            updateState {
                copy(
                    isRefreshing = false,
                    mode = dataMode()
                )
            }
        }
    }

    private fun refreshScheduleState(
        namedScheduleId: Long? = null,
        updating: Boolean = false,
        showLoading: Boolean = true
    ) {
        val previousJob = updateScheduleJob

        val newUpdateScheduleJob = launchToCoroutine(
            handler = errorHandler(
                "refreshScheduleState"
            ) { throwable ->
                viewModelScope.launch {
                    _uiEventFlow.sendErrorEvent(throwable.toAppError())
                }
            }
        ) {
            previousJob?.cancelAndJoin()

            if (showLoading) {
                updateState {
                    copy(mode = ScheduleState.Mode.Loading)
                }
            }
            updateState {
                copy(isRefreshing = updating)
            }

            try {
                refreshNamedScheduleUseCase(
                    namedScheduleId, updating
                )

                if (namedScheduleId != null) namedScheduleRepos.getAndOpen(namedScheduleId)
                else namedScheduleRepos.openDefault()
            } finally {
                updateState {
                    copy(
                        isRefreshing = false,
                        mode = if (showLoading) dataMode() else mode
                    )
                }
            }
        }
        updateScheduleJob = newUpdateScheduleJob
    }

    private fun handleFetchEvent(
        fetchScheduleEvent: FetchScheduleEvent
    ) {
        val previousJob = fetchScheduleJob

        val fetchJob = launchToCoroutine(
            handler = errorHandler(
                "handleFetchEvent"
            ) { throwable ->
                updateState {
                    copy(
                        mode = ScheduleState.Mode.Error(throwable.toAppError())
                    )
                }
            },
        ) {
            previousJob?.cancelAndJoin()

            updateState {
                copy(mode = ScheduleState.Mode.Loading)
            }

            namedScheduleRepos.getAndOpen(
                name = fetchScheduleEvent.name,
                apiId = fetchScheduleEvent.apiId,
                type = fetchScheduleEvent.type
            )

            updateState {
                copy(mode = dataMode())
            }
        }

        fetchScheduleJob = fetchJob
    }

    private fun onSetNamedSchedule(
        namedScheduleId: Long,
    ) {
        viewModelScope.launch {
            namedScheduleRepos.getAndOpen(namedScheduleId)
        }
    }

    private fun saveCurrentNamedSchedule() {
        viewModelScope.launch {
            namedScheduleRepos.saveOpened()
        }
    }

    private fun deleteNamedSchedule() {
        val current = screenState.namedScheduleWithSchedules ?: return

        updateState {
            copy(
                scheduleSheet = false
            )
        }

        viewModelScope.launch {
            namedScheduleRepos.openDefault()

            deleteNamedScheduleUseCase(
                current.namedSchedule.id,
                current.namedSchedule.isDefault
            )
        }
    }

    private fun onSetDefaultSchedule(
        scheduleId: Long,
        timetableId: String
    ) {
        val current = screenState.namedScheduleWithSchedules ?: return

        viewModelScope.launch {
            setDefaultScheduleUseCase(
                currentNamedScheduleWithSchedules = current,
                scheduleId = scheduleId,
                isSaved = screenState.isSaved,
                timetableId = timetableId
            )?.let { result ->
                namedScheduleRepos.openTemp(result)
            }
        }
    }

    private fun onDeleteEvent(eventId: Long) {
        viewModelScope.launch {
            eventDataSource.deleteById(eventId)
        }
    }

    private fun onHideEvent(eventId: Long) {
        viewModelScope.launch {
            eventDataSource.updateIsHidden(eventId, true)
        }
    }
}
