package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.app.extension.getGroupedEvents
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.preferences.EventView
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.EventAction
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ScheduleUiDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleListView(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,

    scheduleUiState: ScheduleUiState,

    isSavedSchedule: Boolean,
    namedScheduleEntity: NamedScheduleEntity,
    scheduleUiDto: ScheduleUiDto,

    eventView: EventView,
    paddingBottom: Dp
) {
    if (scheduleUiDto.fullEventList.isNotEmpty()) {
        LazyColumn(
            state = scheduleUiState.scheduleListState,
            contentPadding = PaddingValues(bottom = paddingBottom),
            modifier = Modifier.fillMaxSize(),
        ) {
            scheduleUiDto.fullEventList.forEachIndexed { index, events ->
                val eventsForDay = events.second.getGroupedEvents().toList()
                stickyHeader {
                    DateHeader(
                        date = events.first,
                        formatter = dayMonthNameFormatter
                    )
                }

                items(
                    items = eventsForDay,
                    key = if (isSavedSchedule) {
                        {
                            Pair(it.second.first().id, it.second.first().startDatetime)
                        }
                    } else null
                ) { eventsGrouped ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        com.egormelnikoff.schedulerutmiit.ui.screens.schedule.Event(
                            navigateToEvent = { scheduleEntity, isSavedSchedule, event, eventExtraData ->
                                appBackStack.openDialog(
                                    Route.Dialog.EventDialog(
                                        namedScheduleEntity = namedScheduleEntity,
                                        scheduleEntity = scheduleEntity,
                                        isSavedSchedule = isSavedSchedule,
                                        event = event,
                                        eventExtraData = eventExtraData
                                    )
                                )
                            },
                            navigateToEditEvent = { scheduleEntity, event ->
                                appBackStack.openDialog(
                                    Route.Dialog.AddEventDialog(
                                        namedScheduleEntity, scheduleEntity, event
                                    )
                                )
                            },
                            onDeleteEvent = { scheduleEntity, eventId ->
                                scheduleViewModel.eventAction(
                                    scheduleEntity,
                                    eventId,
                                    EventAction.Delete
                                )
                            },
                            onUpdateHiddenEvent = { scheduleEntity, event ->
                                scheduleViewModel.eventAction(
                                    scheduleEntity,
                                    event,
                                    EventAction.UpdateHidden(true)
                                )
                            },
                            events = eventsGrouped.second,
                            scheduleEntity = scheduleUiDto.scheduleEntity,
                            eventsExtraData = scheduleUiDto.eventsExtraData,
                            isSavedSchedule = isSavedSchedule,
                            eventView = eventView
                        )
                    }
                    if (index != scheduleUiDto.fullEventList.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    } else {
        Empty(
            title = "¯\\_(ツ)_/¯",
            subtitle = stringResource(R.string.no_classes),
            isBoldTitle = false,
            paddingBottom = paddingBottom
        )
    }
}

@Composable
fun DateHeader(
    date: LocalDate,
    formatter: DateTimeFormatter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val color = MaterialTheme.colorScheme.primary
        Icon(
            modifier = Modifier.width(16.dp),
            imageVector = ImageVector.vectorResource(R.drawable.calendar),
            contentDescription = null,
            tint = color
        )
        Text(
            text = "${
                date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    Locale.getDefault()
                ).replaceFirstChar { it.uppercase() }
            }, ${date.format(formatter)}",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}