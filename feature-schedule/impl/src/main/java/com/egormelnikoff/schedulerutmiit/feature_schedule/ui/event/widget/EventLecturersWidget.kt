package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints.personImageUrl
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AsyncImage
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState


fun LazyListScope.eventLecturersWidget(
    eventState: EventState.Loaded,
    onAction: (EventAction) -> Unit
) {
    if (!eventState.event.lecturers.isNullOrEmpty()) {
        val lecturers = eventState.event.lecturers
        item {
            Box(
                modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16)
            ) {
                ColumnGroup(
                    title = stringResource(R.string.lecturers),
                    titleColor = AppTheme.colors.primary,
                    items = lecturers.map { lecturer ->
                        {
                            AppCell(
                                contentPadding = PaddingValues(
                                    horizontal = AppTheme.paddings.padding12,
                                    vertical = AppTheme.paddings.padding8
                                ),
                                start = {
                                    AsyncImage(
                                        imageUrl = personImageUrl(personId = lecturer.id),
                                        imageSize = 36.dp,
                                        imageTitle = lecturer.fullFio
                                    )
                                },
                                center = {
                                    TextBlock(
                                        text = lecturer.fullFio,
                                        maxLines = 2,
                                    )
                                },
                                onLongClick = {
                                    onAction(EventAction.OnLecturerLongClicked(lecturer))
                                },
                                onClick = if (!eventState.event.isCustomEvent) {
                                    {
                                        onAction(EventAction.OnLecturerClicked(lecturer))
                                    }
                                } else null,
                            )
                        }
                    }
                )
            }
        }

    }
}