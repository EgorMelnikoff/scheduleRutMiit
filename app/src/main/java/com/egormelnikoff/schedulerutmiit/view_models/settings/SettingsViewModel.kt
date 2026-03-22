package com.egormelnikoff.schedulerutmiit.view_models.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.app.preferences.EventView
import com.egormelnikoff.schedulerutmiit.app.preferences.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logger: Logger,
    private val preferencesDataStore: PreferencesDataStore,
) : ViewModel() {
    private val _appSettings = MutableStateFlow<AppSettings?>(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings

    init {
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


    fun sendLogsFile() {
        logger.sendLogsFile()
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

            val themeFlow = combine(
                preferencesDataStore.themeFlow,
                preferencesDataStore.decorColorFlow,
            ) { theme, decorColorIndex ->
                Pair(theme, decorColorIndex)
            }

            val scheduleFlow = combine(
                preferencesDataStore.scheduleViewFlow,
                preferencesDataStore.schedulesDeletableFlow,
                preferencesDataStore.eventCountViewFlow
            ) { scheduleView, schedulesDeletable, eventCountView ->
                Triple(scheduleView, schedulesDeletable, eventCountView)
            }

            combine(
                themeFlow,
                eventFlow,
                scheduleFlow,
                preferencesDataStore.syncTagCommentsFlow,
            ) { theme, eventView, scheduleSettings, syncTagsFlow ->
                AppSettings(
                    theme = theme.first,
                    decorColorIndex = theme.second,
                    scheduleView = scheduleSettings.first,
                    schedulesDeletable = scheduleSettings.second,
                    eventsCountView = scheduleSettings.third,
                    syncTagsAndComments = syncTagsFlow,
                    eventView = eventView
                )
            }.collect { settings ->
                _appSettings.value = settings
            }
        }
    }

    fun onSetEventGroupVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventGroupVisibility(visible)
        }
    }

    fun onSetEventView(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventGroupVisibility(visible)
            preferencesDataStore.setEventRoomsVisibility(visible)
            preferencesDataStore.setEventLecturersVisibility(visible)
            preferencesDataStore.setEventTagVisibility(visible)
            preferencesDataStore.setEventCommentVisibility(visible)
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
        theme: String
    ) {
        viewModelScope.launch {
            preferencesDataStore.setTheme(theme)
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

    fun onSetSyncTagsComments(isSynchronizable: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setSyncTagsComments(isSynchronizable)
        }
    }
}