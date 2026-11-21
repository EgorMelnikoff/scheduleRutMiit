package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.EventTimeGroupVo
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.ScheduleVo
import java.time.LocalDate

private sealed interface ListEntry {
    data class Header(val date: LocalDate) : ListEntry
    data class EventItem(
        val key: Any,
        val timeGroup: EventTimeGroupVo
    ) : ListEntry
}

@Composable
fun ScheduleListWidget(
    scheduleListState: LazyListState,
    isSavedSchedule: Boolean,
    scheduleVo: ScheduleVo,
    eventView: EventView,
    paddingBottom: Dp,
    onAction: (ScheduleAction) -> Unit
) {
    val listItems = remember(scheduleVo.fullEventList) {
        buildList {
            scheduleVo.fullEventList.forEach { (date, timeGroups) ->
                add(ListEntry.Header(date))

                timeGroups.forEach { timeGroup ->
                    val firstEvent = timeGroup.events.firstOrNull()
                    val itemKey = if (isSavedSchedule && firstEvent != null) {
                        "${firstEvent.id}_${firstEvent.startDatetime}"
                    } else {
                        timeGroup.hashCode()
                    }

                    add(ListEntry.EventItem(key = itemKey, timeGroup = timeGroup))
                }
            }
        }
    }

    if (listItems.isNotEmpty()) {
        LazyColumn(
            state = scheduleListState,
            contentPadding = PaddingValues(
                start = AppTheme.paddings.padding16,
                end = AppTheme.paddings.padding16,
                bottom = paddingBottom
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            listItems.forEach { item ->
                when (item) {
                    is ListEntry.Header -> {
                        scheduleListStickyHeaderWidget(
                            item.date, dayMonthNameFormatter
                        )
                    }

                    is ListEntry.EventItem -> {
                        item(key = item.key) {
                            ScheduleItemWidget(
                                timeGroup = item.timeGroup,
                                eventView = eventView,
                                isSavedSchedule = isSavedSchedule,
                                onAction = onAction
                            )
                            SpacerHeight(AppTheme.paddings.padding8)
                        }
                    }
                }
            }
        }
    } else {
        EmptyScreen(
            title = "¯\\_(ツ)_/¯",
            subtitle = stringResource(R.string.no_classes),
            isBoldTitle = false,
            paddingBottom = paddingBottom
        )
    }
}
