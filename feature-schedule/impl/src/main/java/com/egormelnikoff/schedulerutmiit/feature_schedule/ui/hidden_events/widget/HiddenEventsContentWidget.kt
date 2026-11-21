package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.hidden_events.widget

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.hidden_events.HiddenEventsAction

fun LazyListScope.hiddenEventsContentWidget(
    hiddenEvents: List<Event>,
    onAction: (HiddenEventsAction) -> Unit
) {
    items(hiddenEvents) { event ->
        HiddenEventItemWidget(
            event, onAction
        )
    }
}