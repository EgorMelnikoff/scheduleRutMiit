package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.hourMinuteFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import java.time.format.TextStyle
import androidx.compose.ui.platform.LocalLocale

@Composable
fun HiddenEventsDialog(
    namedSchedule: NamedSchedule,
    schedule: Schedule?,
    hiddenEvents: List<Event>,
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = stringResource(R.string.hidden_events),
                subtitleText = "${namedSchedule.shortName} (${schedule?.timetableType?.typeName})",
                navAction = {
                    appBackStack.onBack()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding(),
                start = 16.dp, end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (hiddenEvents.isNotEmpty()) {
                items(hiddenEvents) { event ->
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        val startTime = event.startDatetime
                            .toLocalTimeWithTimeZone()
                            .format(hourMinuteFormatter)
                        val endTime = event.endDatetime
                            .toLocalTimeWithTimeZone()
                            .format(hourMinuteFormatter)

                        val day = if (event.recurrenceRule != null) {
                            event.startDatetime.dayOfWeek.getDisplayName(
                                TextStyle.FULL,
                                LocalLocale.current.platformLocale
                            ).replaceFirstChar { c -> c.uppercase() }
                        } else {
                            event.startDatetime.format(dayMonthYearFormatter)
                        }
                        ClickableItem(
                            title = event.name,
                            titleMaxLines = 1,
                            subtitle = "${event.typeName} ($day, $startTime - $endTime)",
                            subtitleMaxLines = 2,
                            showClickLabel = false,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        schedule?.let {
                                            scheduleViewModel.eventAction(
                                                schedule,
                                                event,
                                                EventAction.UpdateHidden(false)
                                            )
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors()
                                        .copy(
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(24.dp),
                                        imageVector = ImageVector.vectorResource(
                                            R.drawable.visibility
                                        ),
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                }
            } else {
                item {
                    Empty(
                        modifier = Modifier.fillParentMaxSize(),
                        title = "¯\\_(ツ)_/¯",
                        subtitle = stringResource(R.string.empty_here),
                        isBoldTitle = false,
                        paddingBottom = innerPadding.calculateBottomPadding()
                    )
                }
            }
        }
    }
}