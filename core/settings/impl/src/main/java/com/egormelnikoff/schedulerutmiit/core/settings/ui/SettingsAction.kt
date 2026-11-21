package com.egormelnikoff.schedulerutmiit.core.settings.ui

import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.Theme

sealed interface SettingsAction {
    data class OnSetEventGroupsVisibility(val visible: Boolean) : SettingsAction
    data class OnSetEventRoomsVisibility(val visible: Boolean) : SettingsAction
    data class OnSetEventLecturersVisibility(val visible: Boolean) : SettingsAction
    data class OnSetEventTagVisibility(val visible: Boolean) : SettingsAction
    data class OnSetEventCommentVisibility(val visible: Boolean) : SettingsAction
    data class OnSetEventsCountView(val eventsCountView: EventsCountView) : SettingsAction
    data class OnSetTheme(val theme: Theme) : SettingsAction
    data class OnSetUsedImageInReview(val usedImage: Boolean) : SettingsAction
    data class OnSetUsedAmoled(val usedAmoled: Boolean) : SettingsAction
    data class OnSetDecorColor(val decorColor: Int) : SettingsAction
    data class OnSetScheduleView(val scheduleView: ScheduleView) : SettingsAction
    data class OnSetSchedulesDeletable(val isDeletable: Boolean) : SettingsAction
    data class OnSetEventExtraPolicy(val eventExtraPolicy: EventExtraPolicy) : SettingsAction
    data object OnSkipWelcomePage : SettingsAction
    data class OnSetActiveDialog(val activeDialog: SettingsDialog?) : SettingsAction
    data class OnShowImportDialog(val show: Boolean) : SettingsAction
}