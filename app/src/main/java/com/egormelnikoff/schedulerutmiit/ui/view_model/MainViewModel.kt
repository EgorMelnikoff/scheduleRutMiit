package com.egormelnikoff.schedulerutmiit.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.latest_release.domain.use_case.CheckLatestReleaseUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.event.UiEvent
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkLatestReleaseUseCase: CheckLatestReleaseUseCase,
    private val resourcesManager: ResourcesManager
) : ViewModel() {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState

    private val _uiEventChannel = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEventChannel.asSharedFlow()

    private val checkUpdatesMutex = Mutex()

    init {
        checkUpdates(fetchForce = false)
    }

    val currentDate: StateFlow<LocalDateTime> =
        hourlyTicker()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            )

    fun hourlyTicker(): Flow<LocalDateTime> = flow {
        while (true) {
            val now = LocalDateTime.now()
            emit(now.truncatedTo(ChronoUnit.MINUTES))

            delay(Duration.between(now, now.plusHours(1)).toMillis())
        }
    }.distinctUntilChanged()


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
                        UiEvent.ErrorMessage(
                            resourcesManager.getString(R.string.no_updates)
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
}