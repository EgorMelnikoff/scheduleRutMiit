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
import androidx.glance.color.ColorProvider
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
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.calculateCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.model.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.app.modules.ProviderEntryPoint
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetData
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.ui.theme.Black
import com.egormelnikoff.schedulerutmiit.ui.theme.DarkGrey
import com.egormelnikoff.schedulerutmiit.ui.theme.Grey
import com.egormelnikoff.schedulerutmiit.ui.theme.LightGrey
import com.egormelnikoff.schedulerutmiit.ui.theme.White
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeNeutralSurface
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeYellow
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeNeutralSurface
import com.google.gson.Gson
import dagger.hilt.EntryPoints
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsWidget : GlanceAppWidget() {
    private lateinit var gson: Gson
    private lateinit var widgetDataUpdater: WidgetDataUpdater

    companion object {
        val widget_data_key = stringPreferencesKey("widget_data")
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
            val widgetDataStringTest = prefs[widget_data_key]
            val widgetDataTest = gson.fromJson(
                widgetDataStringTest,
                WidgetData::class.java
            )
            EventsWidgetContent(
                widgetData = widgetDataTest,
                onUpdate = {
                    scope.launch {
                        widgetDataUpdater.updateAll()
                    }
                }
            )
        }
    }

    @Composable
    private fun EventsWidgetContent(
        widgetData: WidgetData?,
        onUpdate: () -> Unit
    ) {
        val today = LocalDateTime.now()

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
                .cornerRadius(16.dp)
                .background(
                    colorProvider = ColorProvider(
                        day = White,
                        night = DarkGrey
                    )
                )
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (widgetData?.settledScheduleEntity != null) {
                val scheduleEntity = widgetData.settledScheduleEntity
                val periodicEvents = widgetData.periodicEventsForCalendar
                val nonPeriodicEvents = widgetData.nonPeriodicEventsForCalendar

                var header = LocalContext.current.getString(R.string.today) +
                        ", ${today.format(formatter)}"
                var eventsForToday = calculateEvents(
                    scheduleEntity = scheduleEntity,
                    date = today.toLocalDate(),
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )

                val isFinishedEvents = eventsForToday.isNotEmpty()
                        && today.toLocalTime()
                            .isAfter(eventsForToday.last().endDatetime!!.toLocaleTimeWithTimeZone())

                if (isFinishedEvents || today.dayOfWeek == DayOfWeek.SUNDAY) {
                    val tomorrow = today.toLocalDate().plusDays(1)
                    eventsForToday = calculateEvents(
                        scheduleEntity = scheduleEntity,
                        date = tomorrow,
                        periodicEvents = periodicEvents,
                        nonPeriodicEvents = nonPeriodicEvents
                    )
                    header = LocalContext.current.getString(R.string.tomorrow) +
                            ", ${tomorrow.format(formatter)}"
                }

                val displayedEvents = eventsForToday
                    .groupBy { event ->
                        Pair(
                            event.startDatetime!!.toLocalTime(),
                            event.endDatetime!!.toLocalTime()
                        )
                    }
                    .toList()


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
                                color = ColorProvider(
                                    day = Black,
                                    night = White
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = subHeader,
                            style = TextStyle(
                                color = ColorProvider(
                                    day = Grey,
                                    night = LightGrey
                                ),
                                fontSize = 10.sp
                            ),
                            maxLines = 1
                        )
                    }
                    Image(
                        modifier = GlanceModifier
                            .clickable {
                                onUpdate()
                            }
                            .size(20.dp),
                        provider = ImageProvider(R.drawable.refresh),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            colorProvider = ColorProvider(
                                day = Black,
                                night = White
                            ),
                        )
                    )
                }
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
                .cornerRadius(12.dp)
                .background(
                    colorProvider = ColorProvider(
                        day = lightThemeNeutralSurface,
                        night = darkThemeNeutralSurface
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
                    color = ColorProvider(
                        day = Black,
                        night = White
                    ),
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
                        colorProvider = ColorProvider(
                            day = Grey,
                            night = LightGrey
                        )
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
                    color = ColorProvider(
                        day = Grey,
                        night = LightGrey
                    ),
                ),
                maxLines = 1
            )
        }
        Text(
            text = event.name.toString(),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(
                    day = Black,
                    night = White
                )
            ),
            maxLines = 1
        )

        if (eventExtraData != null && eventExtraData.comment != "") {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (eventExtraData.tag != 0) {
                    val color = when (eventExtraData.tag) {
                        1 -> darkThemeRed
                        2 -> darkThemeOrange
                        3 -> darkThemeYellow
                        4 -> darkThemeGreen
                        5 -> darkThemeLightBlue
                        6 -> darkThemeBlue
                        7 -> darkThemeViolet
                        8 -> darkThemePink
                        else -> Color.Unspecified
                    }
                    Image(
                        modifier = GlanceModifier.size(8.dp),
                        provider = ImageProvider(R.drawable.circle),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            colorProvider = androidx.glance.unit.ColorProvider(
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
                        color = ColorProvider(
                            day = Grey,
                            night = LightGrey
                        )
                    ),
                    maxLines = 1
                )
            }
        }
    }


    fun calculateEvents(
        scheduleEntity: ScheduleEntity,
        date: LocalDate,
        periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
        nonPeriodicEvents: Map<LocalDate, List<Event>>?
    ): List<Event> {
        val events = periodicEvents?.let {
            val currentWeek = calculateCurrentWeek(
                date = date,
                startDate = scheduleEntity.startDate,
                firstPeriodNumber = scheduleEntity.recurrence!!.firstWeekNumber,
                interval = scheduleEntity.recurrence.interval!!
            )
            periodicEvents[currentWeek]?.filter {
                it.key == date.dayOfWeek
            }!!.values.flatten()
        } ?: nonPeriodicEvents?.filter {
            it.key == date
        }?.values?.flatten()
        ?: emptyList()
        return events
            .filter { !it.isHidden }
            .sortedBy { event -> event.startDatetime!!.toLocalTime() }
    }
}