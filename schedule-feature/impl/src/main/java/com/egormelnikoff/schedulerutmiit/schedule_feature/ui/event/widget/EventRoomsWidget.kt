package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.event.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.event.EventState

fun LazyListScope.eventRoomsWidget(
    eventState: EventState.Loaded
) {
    if (!eventState.event.rooms.isNullOrEmpty()) {
        item {
            val rooms = eventState.event.rooms!!

            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                ColumnGroup(
                    title = stringResource(R.string.room),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = rooms.map { room ->
                        {
                            AppCell(
                                title = room.hint.ifBlank { room.name },
                                titleMaxLines = 2,
                                defaultMinHeight = 32.dp
                            )
                        }
                    }

                )
            }
        }
    }
}