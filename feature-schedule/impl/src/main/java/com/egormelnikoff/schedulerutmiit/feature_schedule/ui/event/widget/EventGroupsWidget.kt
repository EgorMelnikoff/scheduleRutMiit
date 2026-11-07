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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState


fun LazyListScope.eventGroupsWidget(
    eventState: EventState.Loaded,
    onAction: (EventAction) -> Unit
) {
    eventState.event.groups?.let { groups ->
        item {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                ColumnGroup(
                    title = stringResource(R.string.groups),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false,
                    items = listOf {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            groups.forEach { group ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
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
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = group.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onBackground,
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