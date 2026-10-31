package com.egormelnikoff.schedulerutmiit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.AppSettings
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.EventView
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.ThemePreferences
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.Theme
import com.egormelnikoff.schedulerutmiit.core.common.domain.repos.PreferencesRepos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepos: PreferencesRepos
) : ViewModel() {
    private val _appSettings = MutableStateFlow<AppSettings?>(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings

    init {
        collectPreferences()
    }

    private fun collectPreferences() {
        viewModelScope.launch {
            val eventFlow = combine(
                preferencesRepos.groupsVisibilityFlow,
                preferencesRepos.roomsVisibilityFlow,
                preferencesRepos.lecturersVisibilityFlow,
                preferencesRepos.tagVisibilityFlow,
                preferencesRepos.commentVisibilityFlow
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
                preferencesRepos.themeFlow,
                preferencesRepos.decorColorFlow,
                preferencesRepos.usedAmoledFlow
            ) { theme, decorColorIndex, usedAmoled ->
                ThemePreferences(theme, usedAmoled, decorColorIndex)
            }

            val scheduleFlow = combine(
                preferencesRepos.scheduleViewFlow,
                preferencesRepos.schedulesDeletableFlow,
                preferencesRepos.eventCountViewFlow
            ) { scheduleView, schedulesDeletable, eventCountView ->
                Triple(scheduleView, schedulesDeletable, eventCountView)
            }

            val uiFlow = combine(
                preferencesRepos.skipWelcomeFlow,
                preferencesRepos.usedImageInReviewFlow
            ) { skipWelcome, usedImage ->
                Pair(skipWelcome, usedImage)
            }

            combine(
                decorFlow,
                eventFlow,
                scheduleFlow,
                uiFlow,
                preferencesRepos.eventExtraPolicyFlow
            ) { decor, eventView, scheduleSettings, uiFlow, eventExtraPolicy ->
                AppSettings(
                    themePreferences = decor,
                    scheduleView = scheduleSettings.first,
                    eventView = eventView,
                    schedulesDeletable = scheduleSettings.second,
                    eventsCountView = scheduleSettings.third,
                    eventExtraPolicy = eventExtraPolicy,
                    usedImageInReview = uiFlow.second,
                    skipWelcomePage = uiFlow.first
                )
            }.collect { settings ->
                _appSettings.value = settings
            }
        }
    }

    fun skipWelcomePage() {
        viewModelScope.launch {
            preferencesRepos.skipWelcomePage()
        }
    }

    fun onSetEventGroupVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesRepos.setEventGroupVisibility(visible)
        }
    }

    fun onSetEventRoomsVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesRepos.setEventRoomsVisibility(visible)
        }
    }

    fun onSetEventLecturersVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesRepos.setEventLecturersVisibility(visible)
        }
    }

    fun onSetEventTagVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesRepos.setEventTagVisibility(visible)
        }
    }

    fun onSetEventCommentVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesRepos.setEventCommentVisibility(visible)
        }
    }

    fun onSetEventsCountView(
        eventsCountView: EventsCountView
    ) {
        viewModelScope.launch {
            preferencesRepos.setEventCountView(eventsCountView)
        }
    }

    fun onSetTheme(
        theme: Theme
    ) {
        viewModelScope.launch {
            preferencesRepos.setTheme(theme)
        }
    }

    fun onSetUsedImageInReview(
        usedImage: Boolean
    ) {
        viewModelScope.launch {
            preferencesRepos.setUsedImageInReview(usedImage)
        }
    }

    fun onSetUsedAmoled(
        usedAmoled: Boolean
    ) {
        viewModelScope.launch {
            preferencesRepos.setUsedAmoled(usedAmoled)
        }
    }

    fun onSetDecorColor(
        decorColor: Int
    ) {
        viewModelScope.launch {
            preferencesRepos.setDecorColor(decorColor)
        }
    }

    fun onSetScheduleView(
        scheduleView: ScheduleView
    ) {
        viewModelScope.launch {
            preferencesRepos.setScheduleView(scheduleView)
        }
    }

    fun onSetSchedulesDeletable(isDeletable: Boolean) {
        viewModelScope.launch {
            preferencesRepos.setSchedulesDeletable(isDeletable)
        }
    }

    fun onSetEventExtraPolicy(eventExtraPolicy: EventExtraPolicy) {
        viewModelScope.launch {
            preferencesRepos.setEventExtraPolicy(eventExtraPolicy)
        }
    }
}