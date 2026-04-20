package com.egormelnikoff.schedulerutmiit.ui.view_models.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.enums.Theme
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.data.local.preferences.DecorPreferences
import com.egormelnikoff.schedulerutmiit.data.local.preferences.EventView
import com.egormelnikoff.schedulerutmiit.data.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.domain.use_case.updates.CheckLatestReleaseUseCase
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.event.UiEvent
import com.egormelnikoff.schedulerutmiit.ui.view_models.settings.state.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
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
class SettingsViewModel @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val checkLatestReleaseUseCase: CheckLatestReleaseUseCase,
    private val resourcesManager: ResourcesManager
) : ViewModel() {
    private val _appSettings = MutableStateFlow<AppSettings?>(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState


    private val _uiEventChannel = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEventChannel.asSharedFlow()

    private val checkUpdatesMutex = Mutex()

    init {
        checkUpdates(false)
        collectSettings()
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

            val delayMs = Duration.between(now, now.plusHours(1)).toMillis()

            delay(delayMs)
        }
    }.distinctUntilChanged()


    fun checkUpdates(
        fetchForce: Boolean = true
    ) {
        viewModelScope.launch {
            checkUpdatesMutex.withLock {
                _settingsState.update {
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
                    _settingsState.update {
                        it.copy(
                            updatesAvailable = result,
                            isUpdating = false
                        )
                    }
                }
            }
        }
    }

    private fun collectSettings() {
        viewModelScope.launch {
            val eventFlow = combine(
                preferencesDataStore.groupsVisibilityFlow,
                preferencesDataStore.roomsVisibilityFlow,
                preferencesDataStore.lecturersVisibilityFlow,
                preferencesDataStore.tagVisibilityFlow,
                preferencesDataStore.commentVisibilityFlow
            ) { groupsVisibility, roomsVisibility, lecturersVisibility, tagVisibility, commentVisibility ->
                EventView(
                    groupsVisible = groupsVisibility,
                    roomsVisible = roomsVisibility,
                    lecturersVisible = lecturersVisibility,
                    tagVisible = tagVisibility,
                    commentVisible = commentVisibility
                )
            }

            val decorFlow = combine(
                preferencesDataStore.themeFlow,
                preferencesDataStore.decorColorFlow,
                preferencesDataStore.usedAmoledFlow
            ) { theme, decorColorIndex, usedAmoled ->
                DecorPreferences(theme, usedAmoled, decorColorIndex)
            }

            val scheduleFlow = combine(
                preferencesDataStore.scheduleViewFlow,
                preferencesDataStore.schedulesDeletableFlow,
                preferencesDataStore.eventCountViewFlow
            ) { scheduleView, schedulesDeletable, eventCountView ->
                Triple(scheduleView, schedulesDeletable, eventCountView)
            }

            combine(
                decorFlow,
                eventFlow,
                scheduleFlow,
                preferencesDataStore.eventExtraPolicyFlow,
                preferencesDataStore.skipWelcomeFLow
            ) { decor, eventView, scheduleSettings, eventExtraPolicy, skipWelcome ->
                AppSettings(
                    decorPreferences = decor,
                    scheduleView = scheduleSettings.first,
                    eventView = eventView,
                    schedulesDeletable = scheduleSettings.second,
                    eventsCountView = scheduleSettings.third,
                    eventExtraPolicy = eventExtraPolicy,
                    skipWelcomePage = skipWelcome
                )
            }.collect { settings ->
                _appSettings.value = settings
            }
        }
    }

    fun skipWelcomePage() {
        viewModelScope.launch {
            preferencesDataStore.skipWelcomePage()
        }
    }

    fun onSetEventGroupVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventGroupVisibility(visible)
        }
    }

    fun onSetEventRoomsVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventRoomsVisibility(visible)
        }
    }

    fun onSetEventLecturersVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventLecturersVisibility(visible)
        }
    }

    fun onSetEventTagVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventTagVisibility(visible)
        }
    }

    fun onSetEventCommentVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventCommentVisibility(visible)
        }
    }

    fun onSetEventsCountView(
        eventsCountView: EventsCountView
    ) {
        viewModelScope.launch {
            preferencesDataStore.setEventCountView(eventsCountView)
        }
    }

    fun onSetTheme(
        theme: Theme
    ) {
        viewModelScope.launch {
            preferencesDataStore.setTheme(theme)
        }
    }

    fun onSetUsedAmoled(
        usedAmoled: Boolean
    ) {
        viewModelScope.launch {
            preferencesDataStore.setUsedAmoled(usedAmoled)
        }
    }

    fun onSetDecorColor(
        decorColor: Int
    ) {
        viewModelScope.launch {
            preferencesDataStore.setDecorColor(decorColor)
        }
    }

    fun onSetScheduleView(
        scheduleView: ScheduleView
    ) {
        viewModelScope.launch {
            preferencesDataStore.setScheduleView(scheduleView)
        }
    }

    fun onSetSchedulesDeletable(isDeletable: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setSchedulesDeletable(isDeletable)
        }
    }

    fun onSetEventExtraPolicy(eventExtraPolicy: EventExtraPolicy) {
        viewModelScope.launch {
            preferencesDataStore.setEventExtraPolicy(eventExtraPolicy)
        }
    }
}