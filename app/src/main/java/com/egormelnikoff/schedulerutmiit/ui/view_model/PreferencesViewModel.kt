package com.egormelnikoff.schedulerutmiit.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.common.enums.Theme
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataSource
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.DecorPreferences
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.EventView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : ViewModel() {
    private val _appSettings = MutableStateFlow<AppSettings?>(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings

    init {
        collectPreferences()
    }

    private fun collectPreferences() {
        viewModelScope.launch {
            val eventFlow = combine(
                preferencesDataSource.groupsVisibilityFlow,
                preferencesDataSource.roomsVisibilityFlow,
                preferencesDataSource.lecturersVisibilityFlow,
                preferencesDataSource.tagVisibilityFlow,
                preferencesDataSource.commentVisibilityFlow
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
                preferencesDataSource.themeFlow,
                preferencesDataSource.decorColorFlow,
                preferencesDataSource.usedAmoledFlow
            ) { theme, decorColorIndex, usedAmoled ->
                DecorPreferences(theme, usedAmoled, decorColorIndex)
            }

            val scheduleFlow = combine(
                preferencesDataSource.scheduleViewFlow,
                preferencesDataSource.schedulesDeletableFlow,
                preferencesDataSource.eventCountViewFlow
            ) { scheduleView, schedulesDeletable, eventCountView ->
                Triple(scheduleView, schedulesDeletable, eventCountView)
            }

            combine(
                decorFlow,
                eventFlow,
                scheduleFlow,
                preferencesDataSource.eventExtraPolicyFlow,
                preferencesDataSource.skipWelcomeFlow
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
            preferencesDataSource.skipWelcomePage()
        }
    }

    fun onSetEventGroupVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataSource.setEventGroupVisibility(visible)
        }
    }

    fun onSetEventRoomsVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataSource.setEventRoomsVisibility(visible)
        }
    }

    fun onSetEventLecturersVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataSource.setEventLecturersVisibility(visible)
        }
    }

    fun onSetEventTagVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataSource.setEventTagVisibility(visible)
        }
    }

    fun onSetEventCommentVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataSource.setEventCommentVisibility(visible)
        }
    }

    fun onSetEventsCountView(
        eventsCountView: EventsCountView
    ) {
        viewModelScope.launch {
            preferencesDataSource.setEventCountView(eventsCountView)
        }
    }

    fun onSetTheme(
        theme: Theme
    ) {
        viewModelScope.launch {
            preferencesDataSource.setTheme(theme)
        }
    }

    fun onSetUsedAmoled(
        usedAmoled: Boolean
    ) {
        viewModelScope.launch {
            preferencesDataSource.setUsedAmoled(usedAmoled)
        }
    }

    fun onSetDecorColor(
        decorColor: Int
    ) {
        viewModelScope.launch {
            preferencesDataSource.setDecorColor(decorColor)
        }
    }

    fun onSetScheduleView(
        scheduleView: ScheduleView
    ) {
        viewModelScope.launch {
            preferencesDataSource.setScheduleView(scheduleView)
        }
    }

    fun onSetSchedulesDeletable(isDeletable: Boolean) {
        viewModelScope.launch {
            preferencesDataSource.setSchedulesDeletable(isDeletable)
        }
    }

    fun onSetEventExtraPolicy(eventExtraPolicy: EventExtraPolicy) {
        viewModelScope.launch {
            preferencesDataSource.setEventExtraPolicy(eventExtraPolicy)
        }
    }
}