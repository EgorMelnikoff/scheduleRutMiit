package com.egormelnikoff.schedulerutmiit.core.common.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.Theme
import kotlinx.coroutines.flow.Flow

interface PreferencesRepos {
    suspend fun setTheme(theme: Theme)
    suspend fun setUsedImageInReview(usedImage: Boolean)
    suspend fun setUsedAmoled(usedAmoled: Boolean)
    suspend fun setDecorColor(color: Int)
    suspend fun setScheduleView(scheduleView: ScheduleView)
    suspend fun setSchedulesDeletable(isDeletable: Boolean)
    suspend fun skipWelcomePage()
    suspend fun setEventExtraPolicy(eventExtraPolicy: EventExtraPolicy)
    suspend fun setEventGroupVisibility(visible: Boolean)
    suspend fun setEventRoomsVisibility(visible: Boolean)
    suspend fun setEventLecturersVisibility(visible: Boolean)
    suspend fun setEventTagVisibility(visible: Boolean)
    suspend fun setEventCommentVisibility(visible: Boolean)
    suspend fun setEventCountView(eventsCountView: EventsCountView)

    val themeFlow: Flow<Theme>
    val usedImageInReviewFlow: Flow<Boolean>
    val usedAmoledFlow: Flow<Boolean>
    val decorColorFlow: Flow<Int>
    val skipWelcomeFlow: Flow<Boolean>
    val scheduleViewFlow: Flow<ScheduleView>
    val schedulesDeletableFlow: Flow<Boolean>
    val eventExtraPolicyFlow: Flow<EventExtraPolicy>
    val eventCountViewFlow: Flow<EventsCountView>
    val groupsVisibilityFlow: Flow<Boolean>
    val roomsVisibilityFlow: Flow<Boolean>
    val lecturersVisibilityFlow: Flow<Boolean>
    val tagVisibilityFlow: Flow<Boolean>
    val commentVisibilityFlow: Flow<Boolean>
}