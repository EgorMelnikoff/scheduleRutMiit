package com.egormelnikoff.schedulerutmiit.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeYellow
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleState

@Composable
fun CommentsDialog(
    scheduleState: ScheduleState.Loaded,
    showTags: Boolean
) {
    val eventsExtraGroupedByName =
        remember(scheduleState.selectedSchedule!!.eventsExtraData) {
            scheduleState.selectedSchedule.eventsExtraData
                .filter { it.comment.isNotEmpty() }
                .groupBy { event -> event.eventName }
                .toList()
                .sortedBy { (eventName, _) -> eventName }
        }
    if (eventsExtraGroupedByName.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(eventsExtraGroupedByName) { eventsExtraGrouped ->
                EventExtra(
                    showTags = showTags,
                    eventsExtraData = eventsExtraGrouped.second
                )
            }
        }
    } else {
        Empty(
            title = LocalContext.current.getString(R.string.no_comments),
            subtitle = LocalContext.current.getString(R.string.comment_add)
        )
    }
}

@Composable
fun EventExtra(
    eventsExtraData: List<EventExtraData>,
    showTags: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = eventsExtraData.first().eventName.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline,
            thickness = 0.5.dp
        )
        eventsExtraData.forEach { eventExtraData ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val color = when {
                    !showTags -> MaterialTheme.colorScheme.onBackground
                    eventExtraData.tag == 1 -> lightThemeRed
                    eventExtraData.tag == 2 -> lightThemeOrange
                    eventExtraData.tag == 3 -> lightThemeYellow
                    eventExtraData.tag == 4 -> lightThemeGreen
                    eventExtraData.tag == 5 -> lightThemeLightBlue
                    eventExtraData.tag == 6 -> lightThemeBlue
                    eventExtraData.tag == 7 -> lightThemeViolet
                    eventExtraData.tag == 8 -> lightThemePink
                    else -> MaterialTheme.colorScheme.onBackground
                }
                Comment(
                    message = eventExtraData.comment,
                    color = color
                )
            }
        }
    }
}