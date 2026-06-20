package com.egormelnikoff.schedulerutmiit.ui.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.ui.event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.event.UiText
import com.egormelnikoff.schedulerutmiit.export.domain.use_case.ExportDataUseCase
import com.egormelnikoff.schedulerutmiit.export.domain.use_case.ImportDataUseCase
import com.egormelnikoff.schedulerutmiit.latest_release.domain.use_case.CheckLatestReleaseUseCase
import com.egormelnikoff.schedulerutmiit.ui.view_model.state.AppState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkLatestReleaseUseCase: CheckLatestReleaseUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val exportDataUseCase: ExportDataUseCase
) : ViewModel() {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState

    private val _uiEventChannel = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEventChannel.asSharedFlow()

    private val checkUpdatesMutex = Mutex()

    init {
        checkUpdates(fetchForce = false)
    }

    private fun tickerFlow(intervalMillis: Long = 5 * 60_000L): Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(intervalMillis.milliseconds)
        }
    }

    private val timeFlow: Flow<LocalDateTime> =
        tickerFlow()
            .map { LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) }
            .distinctUntilChanged()


    val hourlyDateTime: StateFlow<LocalDateTime> =
        timeFlow
            .map { it.truncatedTo(ChronoUnit.HOURS) }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
            )

    fun checkUpdates(
        fetchForce: Boolean = true
    ) {
        viewModelScope.launch {
            checkUpdatesMutex.withLock {
                _appState.update {
                    it.copy(
                        updatesAvailable = false,
                        isUpdating = true
                    )
                }
                checkLatestReleaseUseCase(fetchForce).let { result ->
                    if (!result && fetchForce) _uiEventChannel.emit(
                        UiEvent.InfoMessage(
                            UiText.StringResource(R.string.no_updates),
                            false
                        )
                    )
                    _appState.update {
                        it.copy(
                            updatesAvailable = result,
                            isUpdating = false
                        )
                    }
                }
            }
        }
    }

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            when (val result = exportDataUseCase(uri)) {
                is Result.Error -> {
                    _uiEventChannel.emit(
                        UiEvent.ErrorMessage(result.typedError)
                    )
                }

                is Result.Success -> {
                    _uiEventChannel.emit(
                        UiEvent.InfoMessage(
                            UiText.StringResource(R.string.success)
                        )
                    )
                }
            }
        }
    }

    fun importData(uri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (uri == null) {
                _uiEventChannel.emit(
                    UiEvent.ErrorMessage(TypedError.EmptyBodyError)
                )
                return@launch
            }
            when (val result = importDataUseCase(uri)) {
                is Result.Error -> {
                    _uiEventChannel.emit(
                        UiEvent.ErrorMessage(result.typedError)
                    )
                }

                is Result.Success -> {
                    onSuccess()
                    _uiEventChannel.emit(
                        UiEvent.InfoMessage(
                            UiText.StringResource(R.string.success)
                        )
                    )
                }
            }
        }
    }
}