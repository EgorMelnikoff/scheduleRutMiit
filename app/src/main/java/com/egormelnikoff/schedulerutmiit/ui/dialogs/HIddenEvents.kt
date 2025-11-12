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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HiddenEventsDialog(
    onBack: () -> Unit,
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    onShowEvent: (Long) -> Unit,
    hiddenEvents: List<Event>,
    eventsExtraData: List <EventExtraData>,
    externalPadding: PaddingValues
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, hh:MM")

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = LocalContext.current.getString(R.string.hidden_events),
                navAction = {
                    onBack()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = externalPadding.calculateBottomPadding(),
                start = 16.dp, end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (hiddenEvents.isNotEmpty()) {
                items(hiddenEvents) { event ->
                    val eventExtraData = eventsExtraData.find { it.id == event.id }
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
                            onClick = {
                                navigateToEvent(Pair(event, eventExtraData))
                            },
                            showClickLabel = false,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        onShowEvent(event.id)
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
                        subtitle = LocalContext.current.getString(R.string.empty_here),
                        isBoldTitle = false,
                        paddingBottom = externalPadding.calculateBottomPadding()
                    )
                }
            }
        }
    }
}