package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.widget

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.HiddenEventsAction

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