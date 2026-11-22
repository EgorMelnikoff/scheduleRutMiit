package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState

internal fun LazyListScope.eventRoomsWidget(
    eventState: EventState.Loaded,
    onAction: (EventAction) -> Unit
) {
    if (!eventState.event.rooms.isNullOrEmpty()) {
        val rooms = eventState.event.rooms
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
                                contentPadding = PaddingValues(
                                    horizontal = AppTheme.paddings.padding12,
                                    vertical = AppTheme.paddings.padding16,
                                ),
                                center = {
                                    TextBlock(
                                        text = room.hint.ifBlank { room.name },
                                        maxLines = 2,
                                    )
                                },
                                onLongClick = {
                                    onAction(EventAction.OnRoomLongClicked(room))
                                },
                            )
                        }
                    }

                )
            }
        }
    }
}