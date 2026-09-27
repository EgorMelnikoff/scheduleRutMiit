package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints.personImageUrl
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.LeadingAsyncImage
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenState


fun LazyListScope.eventLecturersWidget(
    eventScreenState: EventScreenState.Loaded,
    onAction: (EventScreenAction) -> Unit
) {
    if (!eventScreenState.event.lecturers.isNullOrEmpty()) {
        item {
            val lecturers = eventScreenState.event.lecturers!!

            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                ColumnGroup(
                    title = stringResource(R.string.lecturers),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = lecturers.map { lecturer ->
                        {
                            AppCell(
                                title = lecturer.fullFio,
                                titleMaxLines = 2,
                                defaultMinHeight = 32.dp,
                                onLongClick = {
                                    onAction(EventScreenAction.OnLecturerLongClicked(lecturer))
                                },
                                onClick = if (!eventScreenState.event.isCustomEvent) {
                                    {
                                        onAction(EventScreenAction.OnLecturerClicked(lecturer))
                                    }
                                } else null,
                                leadingIcon = {
                                    LeadingAsyncImage(
                                        title = lecturer.fullFio,
                                        imageUrl = personImageUrl(personId = lecturer.id)
                                    )
                                }
                            )
                        }
                    }
                )
            }
        }

    }
}