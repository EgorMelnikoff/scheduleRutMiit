package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState


fun LazyListScope.eventGroupsWidget(
    eventState: EventState.Loaded,
    onAction: (EventAction) -> Unit
) {
    eventState.event.groups?.let { groups ->
        item {
            Box(
                modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16)
            ) {
                ColumnGroup(
                    title = stringResource(R.string.groups),
                    titleColor = AppTheme.colors.primary,
                    withBackground = false,
                    items = listOf {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                        ) {
                            groups.forEach { group ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(AppTheme.shapes.extraSmall)
                                        .background(AppTheme.colors.secondaryContainer)
                                        .defaultMinSize(minWidth = 80.dp)
                                        .let {
                                            if (!eventState.event.isCustomEvent) {
                                                it.combinedClickable(
                                                    onClick = {
                                                        onAction(
                                                            EventAction.OnGroupClicked(
                                                                group
                                                            )
                                                        )
                                                    },
                                                    onLongClick = {
                                                        onAction(
                                                            EventAction.OnGroupLongClicked(
                                                                group
                                                            )
                                                        )
                                                    }
                                                )
                                            } else it
                                        }
                                        .padding(AppTheme.paddings.padding8)
                                ) {
                                    Text(
                                        text = group.name,
                                        style = AppTheme.typography.titleSmall,
                                        color = AppTheme.colors.onBackground,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}