package com.egormelnikoff.schedulerutmiit.ui.schedule

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.composable.GroupItem
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeYellow
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun Event(
    onShowDialogEvent: (Pair<Event, EventExtraData?>) -> Unit,
    events: List<Event>,
    eventsExtraData: List<EventExtraData>,
    isShortEvent: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (events.first().timeSlotName != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = events.first().timeSlotName.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text =
                    "${
                        events.first().startDatetime!!
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime()
                    } - ${
                        events.first().endDatetime!!
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime()
                    }",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        GroupItem(
            items = events.map { event ->
                {
                    SingleEvent(
                        eventExtraData = eventsExtraData.find {
                            it.id == event.id
                        },
                        isShortEvent = isShortEvent,
                        event = event,
                        onShowDialogEvent = onShowDialogEvent
                    )
                }
            }
        )
    }
}

@Composable
fun SingleEvent(
    onShowDialogEvent: (Pair<Event, EventExtraData?>) -> Unit,
    isShortEvent: Boolean,
    event: Event,
    eventExtraData: EventExtraData?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onShowDialogEvent(Pair(event, eventExtraData))
                },
            )
    ) {
        if (eventExtraData != null) {
            Canvas(Modifier.fillMaxWidth()) {
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = size.width, y = 0f),
                    color = when (eventExtraData.tag) {
                        1 -> darkThemeRed
                        2 -> darkThemeOrange
                        3 -> darkThemeYellow
                        4 -> darkThemeGreen
                        5 -> darkThemeLightBlue
                        6 -> darkThemeBlue
                        7 -> darkThemeViolet
                        8 -> darkThemePink
                        else -> Color.Unspecified
                    },
                    strokeWidth = 24f
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = event.typeName.toString(),
                fontSize = 12.sp,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = event.name.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!isShortEvent) {
                if (event.groups!!.isNotEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.group),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        event.groups.forEach { group ->
                            Text(
                                text = group.name!!,
                                fontSize = 12.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                if (event.rooms!!.isNotEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.room),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        event.rooms.forEach { room ->
                            Text(
                                text = room.name!!,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                if (event.lecturers!!.isNotEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.person),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        event.lecturers.forEach { lecturer ->
                            Text(
                                text = lecturer.shortFio!!,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
            if (eventExtraData != null && eventExtraData.comment != "") {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 2.dp),
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp
                )
                Comment(
                    message = eventExtraData.comment
                )
            }
        }
    }
}

@Composable
fun Comment(
    message: String,
    color: Color? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(14.dp),
            imageVector = ImageVector.vectorResource(R.drawable.comment),
            contentDescription = null,
            tint = color ?: MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = message,
            fontSize = 12.sp,
            maxLines = 2,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            overflow = TextOverflow.Ellipsis,
            color = color ?: MaterialTheme.colorScheme.onBackground
        )
    }
}