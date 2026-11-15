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
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
//class ScheduleTestViewModel @Inject constructor(
//    private val refreshNamedScheduleUseCase: RefreshNamedScheduleUseCase,
//
//    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,
//    private val setDefaultScheduleUseCase: SetDefaultScheduleUseCase,
//    private val eventDataSource: EventDataSource,
//    private val namedScheduleRepos: NamedScheduleRepos,
//    private val settingsRepos: SettingsRepos,
//    private val fetchScheduleCache: FetchScheduleCache,
//    private val importDataUseCase: ImportDataUseCase,
//    private val exportDataUseCase: ExportDataUseCase,
//    private val appRouter: AppRouter,
//    private val timeProvider: TimeProvider,
//    private val scheduleUiStateConverter: ScheduleUiStateConverter
//) : BaseViewModel<TestState, ScheduleUiState, ScheduleAction>(TestState()) {
//    private val _screenState = MutableStateFlow(ScreenState())
//    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
//
//    private var fetchScheduleJob: Job? = null
//    private var updateScheduleJob: Job? = null
//
//
//    override fun onAction(action: ScheduleAction) {
//        when (action) {
//            is ScheduleAction.OnAddEventClicked -> onAddEventClicked(action.scheduleId)
//            is ScheduleAction.OnChangeSchedule -> TODO()
//            ScheduleAction.OnDeleteClicked -> TODO()
//            ScheduleAction.OnDeleteSchedule -> TODO()
//            is ScheduleAction.OnEventClicked -> TODO()
//            ScheduleAction.OnHiddenEventsClicked -> TODO()
//            is ScheduleAction.OnHideEventClicked -> onHideEventClicked(action.id)
//            is ScheduleAction.OnHideEvent -> onHideEvent(action.id)
//            ScheduleAction.OnMenuVertClicked -> TODO()
//            ScheduleAction.OnP2R -> TODO()
//            ScheduleAction.OnRenameClicked -> TODO()
//            ScheduleAction.OnSavePdfClicked -> TODO()
//            ScheduleAction.OnTitleClicked -> TODO()
//            is ScheduleAction.OnViewClicked -> onViewClicked(action.view)
//        }
//    }
//
//    override fun onScreenInitialized() {
//        super.onScreenInitialized()
//
//        namedScheduleRepos.observeAll().onEach { schedules ->
//            updateState {
//                copy(namedSchedules = schedules)
//            }
//        }
//            .launchIn(viewModelScope)
//
//        timeProvider.currentHour
//            .onEach { dateTime ->
//                updateState {
//                    copy(
//                        time = dateTime,
//                    )
//                }
//            }
//            .launchIn(viewModelScope)
//
//
//        fetchScheduleCache.scheduleRequests
//            .onEach { event ->
//                handleFetchEvent(event)
//            }.launchIn(viewModelScope)
//
//        namedScheduleRepos.currentNamedSchedule.onEach { current ->
//            updateState {
//                copy(
//                    namedScheduleWithSchedules = current,
//                )
//            }
//        }.launchIn(viewModelScope)
//
//    }
//
//    override fun TestState.toUiState(): ScheduleUiState {
//        return scheduleUiStateConverter.toUiState(this)
//    }
//
//    val uiEvent = _uiEventFlow.asSharedFlow()
//
//
//    private fun onAddEventClicked(
//        scheduleId: Long
//    ) {
//        appRouter.openDialog(
//            Route.Dialog.EditEventDialog(
//                eventId = null,
//                scheduleId = scheduleId
//            )
//        )
//    }
//
//    fun onImportData(uri: Uri?) {
//        viewModelScope.launch {
//            if (uri == null) {
//                _uiEventFlow.sendErrorEvent(AppError.EmptyBodyError)
//                return@launch
//            }
//            try {
//                importDataUseCase(uri)
//                _uiEventFlow.emit(
//                    UiEvent.InfoMessage(
//                        UiText.StringResource(R.string.success)
//                    )
//                )
//                _uiEventFlow.sendInfoEvent(
//                    UiEvent.InfoMessage(
//                        UiText.StringResource(R.string.success)
//                    )
//                )
//            } catch (e: Exception) {
//                _uiEventFlow.sendErrorEvent(e.toAppError())
//            }
//        }
//    }
//
//    fun onExportData(uri: Uri) {
//        viewModelScope.launch {
//            try {
//                exportDataUseCase(uri)
//                _uiEventFlow.sendInfoEvent(
//                    UiEvent.InfoMessage(
//                        UiText.StringResource(R.string.success)
//                    )
//                )
//            } catch (e: Exception) {
//                _uiEventFlow.sendErrorEvent(e.toAppError())
//            }
//
//        }
//    }
//
//    fun onOpenDialog(dialog: Route.Dialog) {
//        appRouter.openDialog(dialog)
//    }
//
//    fun cancelLoading() {
//        viewModelScope.launch {
//            fetchScheduleJob?.cancelAndJoin()
//            updateState {
//                toContent()
//            }
//        }
//    }
//
//    fun cancelRefresh() {
//        viewModelScope.launch {
//            updateScheduleJob?.cancelAndJoin()
//            updateState {
//                toContent()
//            }
//        }
//    }
//
//    fun refreshScheduleState(
//        namedScheduleId: Long? = null,
//        updating: Boolean = false,
//        showLoading: Boolean = true
//    ) {
//        val newUpdateScheduleJob = viewModelScope.launch {
//            updateScheduleJob?.cancelAndJoin()
//            updateState {
//                copy(
//                    mode = , //TODO
//                    isRefreshing = updating
//                )
//            }
//            updateScreenState(
//                isLoading = showLoading,
//                isRefreshing = updating,
//                isError = false
//            )
//            refreshNamedScheduleUseCase(
//                namedScheduleId, updating
//            )
//
//            if (namedScheduleId != null) namedScheduleRepos.getAndOpen(namedScheduleId)
//            else namedScheduleRepos.openDefault()
//
//            updateScreenState(
//                isLoading = false,
//                isRefreshing = false
//            )
//        }
//        updateScheduleJob = newUpdateScheduleJob
//    }
//
//    private fun handleFetchEvent(
//        fetchScheduleEvent: FetchScheduleEvent
//    ) {
//        val fetchJob = launchToCoroutine(
//            handler = errorHandler(
//                "handleFetchEvent"
//            ) { throwable ->
//                viewModelScope.launch {
//                    _uiEventFlow.sendErrorEvent(throwable.toAppError())
//                }
//            },
//        ) {
//            fetchScheduleJob?.cancelAndJoin()
//
//            updateScreenState(
//                isLoading = true
//            )
//
//            namedScheduleRepos.getAndOpen(
//                name = fetchScheduleEvent.name,
//                apiId = fetchScheduleEvent.apiId,
//                type = fetchScheduleEvent.type
//            )
//
//            updateScreenState(
//                isError = false,
//                isLoading = false
//            )
//        }
//
//        fetchScheduleJob = fetchJob
//
//    }
//
//    fun onSetNamedSchedule(
//        namedScheduleId: Long,
//    ) {
//        viewModelScope.launch {
//            namedScheduleRepos.getAndOpen(namedScheduleId)
//        }
//    }
//
//    fun saveCurrentNamedSchedule() {
//        viewModelScope.launch {
//            namedScheduleRepos.saveOpened()
//        }
//    }
//
//    fun deleteNamedSchedule(
//        namedScheduleId: Long,
//        isDefault: Boolean
//    ) {
//        viewModelScope.launch {
//            namedScheduleRepos.openDefault()
//
//            deleteNamedScheduleUseCase(
//                namedScheduleId, isDefault
//            )
//        }
//    }
//
//    fun onSetDefaultSchedule(
//        scheduleId: Long,
//        timetableId: String
//    ) {
//        viewModelScope.launch {
//            setDefaultScheduleUseCase(
//                currentNamedScheduleWithSchedules = currentNamedSchedule(),
//                scheduleId = scheduleId,
//                isSaved = _screenState.value.isSaved,
//                timetableId = timetableId
//            )?.let { result ->
//                namedScheduleRepos.openTemp(result)
//            }
//        }
//    }
//
//    fun onDeleteEvent(eventId: Long) {
//        viewModelScope.launch {
//            eventDataSource.deleteById(eventId)
//        }
//    }
//
//
//    private fun onHideEventClicked(eventId: Long) {
//        updateState {
//            copy(
//                hideEventSheet = eventId
//            )
//        }
//    }
//
//    private fun onHideEvent(eventId: Long) {
//        viewModelScope.launch {
//            eventDataSource.updateIsHidden(eventId, true)
//            updateState {
//                copy(
//                    hideEventSheet = null
//                )
//            }
//        }
//    }
//
//    private fun onViewClicked(view: ScheduleView) {
//        viewModelScope.launch {
//            settingsRepos.setScheduleView(view.next())
//        }
//    }
//
//
//    private fun currentNamedSchedule(): NamedScheduleWithSchedules {
//        val state = screenState
//
//        require(state.mode is TestState.Mode.Content)
//        return requireNotNull(state.namedScheduleWithSchedules)
//    }
//}