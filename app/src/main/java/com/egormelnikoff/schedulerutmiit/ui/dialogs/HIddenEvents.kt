package com.egormelnikoff.schedulerutmiit.ui.dialogs

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
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.extension.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.state.actions.EventActions
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HiddenEventsDialog(
    scheduleEntity: ScheduleEntity?,
    hiddenEvents: List<Event>,
    navigationActions: NavigationActions,
    eventActions: EventActions
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, hh:MM")

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = stringResource(R.string.hidden_events),
                navAction = {
                    navigationActions.onBack()
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
            if (hiddenEvents.isNotEmpty() && scheduleEntity != null) {
                items(hiddenEvents) { event ->
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        ClickableItem(
                            title = event.name!!,
                            titleMaxLines = 1,
                            subtitle = if (event.recurrenceRule != null) {
                                val day = event.startDatetime!!.dayOfWeek.getDisplayName(
                                        java.time.format.TextStyle.FULL,
                                        Locale.getDefault()
                                    ).replaceFirstChar { c -> c.uppercase() }
                                val startTime = event.startDatetime.toLocaleTimeWithTimeZone()
                                        .format(timeFormatter)
                                val endTime = event.endDatetime!!.toLocaleTimeWithTimeZone()
                                        .format(timeFormatter)
                                "${event.typeName} ($day, $startTime - $endTime)"
                            } else {
                                "${event.typeName} (${event.startDatetime!!.format(dateTimeFormatter)})"
                            },
                            subtitleMaxLines = 2,
                            showClickLabel = false,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        eventActions.onShowEvent(scheduleEntity, event.id)
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