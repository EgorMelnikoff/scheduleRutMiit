package com.egormelnikoff.schedulerutmiit.ui.view_model

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.utils.error.AppError
import com.egormelnikoff.schedulerutmiit.core.common.utils.error.toAppError
import com.egormelnikoff.schedulerutmiit.core.common.utils.time_provider.TimeProvider
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiText
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.sendErrorEvent
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.sendInfoEvent
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.latest_release.domain.use_case.CheckLatestReleaseUseCase
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.export.ExportDataUseCase
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.export.ImportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkLatestReleaseUseCase: CheckLatestReleaseUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val exportDataUseCase: ExportDataUseCase,
    timeProvider: TimeProvider
) : BaseViewModel<AppState, MainAction>(AppState()) {

    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEventFlow.asSharedFlow()

    private val checkUpdatesMutex = Mutex()

    val hourlyDateTime: StateFlow<LocalDateTime> = timeProvider.currentHour
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        )

    override fun onScreenInitialized() {
        checkUpdates()
    }

    override fun onAction(action: MainAction) {
        when (action) {
            is MainAction.ExportData -> exportData(action.uri)
            is MainAction.ImportData -> importData(action.uri)
        }
    }

    private fun checkUpdates() {
        viewModelScope.launch {
            checkUpdatesMutex.withLock {
                updateState {
                    copy(updatesAvailable = false)
                }
                try {
                    val result = checkLatestReleaseUseCase()
                    if (!result) {
                        _uiEventFlow.sendInfoEvent(
                            UiEvent.InfoMessage(
                                UiText.StringResource(R.string.no_updates),
                                false
                            )
                        )
                    }
                    updateState {
                        copy(updatesAvailable = result)
                    }
                } catch (e: Exception) {
                    _uiEventFlow.sendErrorEvent(e.toAppError())
                }
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

    private fun importData(uri: Uri?) {
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
}