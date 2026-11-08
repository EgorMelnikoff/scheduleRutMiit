package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState

fun LazyListScope.eventRoomsWidget(
    eventState: EventState.Loaded
) {
    eventState.event.rooms?.let { rooms ->
        item {
            Box(
                modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16)
            ) {
                ColumnGroup(
                    title = stringResource(R.string.room),
                    titleColor = AppTheme.colors.primary,
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