package com.egormelnikoff.schedulerutmiit.core.common.preferences

import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.common.enums.Theme
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    suspend fun setLatestRelease(latestRelease: LatestRelease)
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

    val latestReleaseFlow: Flow<LatestRelease?>
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