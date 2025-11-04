package com.egormelnikoff.schedulerutmiit.app.widget.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.app.modules.ProviderEntryPoint
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetData
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.app.widget.ui.theme.ScheduleGlanceTheme
import com.egormelnikoff.schedulerutmiit.ui.theme.Blue
import com.egormelnikoff.schedulerutmiit.ui.theme.Green
import com.egormelnikoff.schedulerutmiit.ui.theme.LightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.Orange
import com.egormelnikoff.schedulerutmiit.ui.theme.Pink
import com.egormelnikoff.schedulerutmiit.ui.theme.Red
import com.egormelnikoff.schedulerutmiit.ui.theme.Violet
import com.egormelnikoff.schedulerutmiit.ui.theme.Yellow
import com.google.gson.Gson
import dagger.hilt.EntryPoints
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EventsWidget : GlanceAppWidget() {
    private lateinit var gson: Gson
    private lateinit var widgetDataUpdater: WidgetDataUpdater

    companion object {
        val eveningTime: LocalTime = LocalTime.of(18, 0)
        val widgetDataKey = stringPreferencesKey("widget_data")
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val provider = EntryPoints.get(context, ProviderEntryPoint::class.java)
        gson = provider.gson()
        widgetDataUpdater = provider.widgetDataUpdater()

        provideContent {
            val scope = rememberCoroutineScope()
            val prefs = currentState<Preferences>()
            val widgetDataString = prefs[widgetDataKey]
            val widgetData = gson.fromJson(
                widgetDataString,
                WidgetData::class.java
            )
            ScheduleGlanceTheme {
                EventsWidgetContent(
                    widgetData = widgetData,
                    onUpdate = {
                        scope.launch {
                            widgetDataUpdater.updateAll()
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun EventsWidgetContent(
        widgetData: WidgetData?,
        onUpdate: () -> Unit
    ) {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM")

        val subHeader = StringBuilder().apply {
            if (widgetData?.namedSchedule != null) {
                append(widgetData.namedSchedule.namedScheduleEntity.shortName)
            }
            if (widgetData?.settledScheduleEntity != null) {
                append(" (${widgetData.settledScheduleEntity.typeName})")
            }
        }.toString()

        Column(
            modifier = GlanceModifier.Companion
                .fillMaxSize()
                .background(
                    imageProvider = ImageProvider(R.drawable.large_rounded),
                    colorFilter = ColorFilter.tint(
                        GlanceTheme.colors.background
                    )
                )
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (widgetData?.settledScheduleEntity != null) {
                val header = when (widgetData.reviewData.displayedDate) {
                    today -> {
                       "${LocalContext.current.getString(R.string.today)}, " +
                               "${widgetData.reviewData.displayedDate.format(formatter)}"
                    }

                    today.plusDays(1) -> {
                        "${LocalContext.current.getString(R.string.tomorrow)}, " +
                                "${widgetData.reviewData.displayedDate.format(formatter)}"
                    }

                    else -> {
                        widgetData.reviewData.displayedDate.format(formatter)
                    }
                }

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = header,
                            style = TextStyle(
                                color = GlanceTheme.colors.onBackground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = subHeader,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSecondaryContainer,
                                fontSize = 10.sp
                            ),
                            maxLines = 1
                        )
                    }
                    Image(
                        modifier = GlanceModifier
                            .cornerRadius(8.dp)
                            .clickable {
                                onUpdate()
                            }
                            .size(32.dp)
                            .padding(8.dp),
                        provider = ImageProvider(R.drawable.refresh),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            colorProvider = GlanceTheme.colors.onBackground
                        )
                    )
                }
                val displayedEvents = widgetData.reviewData.events.toList()

                if (displayedEvents.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    LazyColumn {
                        itemsIndexed(displayedEvents) { index, events ->
                            Column {
                                Event(
                                    events = events.second,
                                    eventsExtraData = widgetData.eventsExtraData
                                )
                                if (index != displayedEvents.lastIndex) {
                                    Spacer(modifier = GlanceModifier.height(4.dp))
                                }
                            }
                        }
                        item {
                            Spacer(modifier = GlanceModifier.height(16.dp))
                        }
                    }
                } else {
                    Empty(
                        title = LocalContext.current.getString(R.string.empty_day)
                    )
                }
            } else {
                Empty(
                    title = LocalContext.current.getString(R.string.empty_here),
                    onClick = onUpdate
                )
            }
        }
    }

    @Composable
    private fun Event(
        events: List<Event>,
        eventsExtraData: List<EventExtraData>
    ) {
        Row(
            modifier = GlanceModifier.Companion
                .fillMaxWidth()
                .background(
                    imageProvider = ImageProvider(R.drawable.medium_rounded),
                    colorFilter = ColorFilter.tint(
                        colorProvider = GlanceTheme.colors.secondaryContainer
                    )
                )
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "${
                    events.first().startDatetime!!.toLocaleTimeWithTimeZone()
                }\n${
                    events.first().endDatetime!!.toLocaleTimeWithTimeZone()
                }",
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Spacer(
                modifier = GlanceModifier.Companion
                    .width(0.5.dp)
                    .background(
                        colorProvider = GlanceTheme.colors.onSecondaryContainer
                    )

            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Column {
                events.forEachIndexed { index, event ->
                    EventSingle(
                        event = event,
                        eventExtraData = eventsExtraData.find { it.id == event.id }
                    )
                    if (index != events.lastIndex) {
                        Spacer(modifier = GlanceModifier.height(8.dp))
                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun EventSingle(
        event: Event,
        eventExtraData: EventExtraData?
    ) {
        event.typeName?.let {
            Text(
                text = it,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = GlanceTheme.colors.onSecondaryContainer
                ),
                maxLines = 1
            )
        }
        Text(
            text = event.name.toString(),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onBackground
            ),
            maxLines = 1
        )

        if (eventExtraData != null && eventExtraData.comment != "") {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (eventExtraData.tag != 0) {
                    val color = when (eventExtraData.tag) {
                        1 -> Red
                        2 -> Orange
                        3 -> Yellow
                        4 -> Green
                        5 -> LightBlue
                        6 -> Blue
                        7 -> Violet
                        8 -> Pink
                        else -> Color.Unspecified
                    }
                    Image(
                        modifier = GlanceModifier.size(8.dp),
                        provider = ImageProvider(R.drawable.circle),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            colorProvider = ColorProvider(
                                color = color
                            )
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                }
                Text(
                    text = eventExtraData.comment,
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = GlanceTheme.colors.onSecondaryContainer
                    ),
                    maxLines = 1
                )
            }
        }
    }
}