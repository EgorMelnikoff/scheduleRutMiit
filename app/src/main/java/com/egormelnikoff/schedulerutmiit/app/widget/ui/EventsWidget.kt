package com.egormelnikoff.schedulerutmiit.app.widget.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.egormelnikoff.schedulerutmiit.app.widget.data.WidgetData
import com.egormelnikoff.schedulerutmiit.app.widget.ui.theme.ScheduleGlanceTheme
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.extension.replaceDate
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.getColorByIndex
import com.egormelnikoff.schedulerutmiit.di.ProviderEntryPoint
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.findEventExtra
import com.egormelnikoff.schedulerutmiit.schedule.domain.widget.WidgetDataUpdater
import dagger.hilt.EntryPoints
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.time.LocalDate

class EventsWidget : GlanceAppWidget() {
    private lateinit var json: Json
    private lateinit var widgetDataUpdater: WidgetDataUpdater
    private val updateMutex = Mutex()

    companion object {
        val widgetDataKey = stringPreferencesKey("widget_data")
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val provider = EntryPoints.get(context, ProviderEntryPoint::class.java)
        json = provider.json()
        widgetDataUpdater = provider.widgetDataUpdater()

        provideContent {
            val scope = rememberCoroutineScope()
            val prefs = currentState<Preferences>()
            val widgetData = prefs[widgetDataKey]?.let {
                json.decodeFromString<WidgetData>(it)
            }

            ScheduleGlanceTheme {
                EventsWidgetContent(
                    widgetData = widgetData,
                    onUpdate = {
                        scope.launch {
                            updateMutex.withLock {
                                widgetDataUpdater.updateAll()
                            }
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

        val subHeader = StringBuilder().apply {
            if (widgetData?.namedSchedule != null) {
                append(widgetData.namedSchedule.shortName)
            }
            if (widgetData?.settledSchedule != null) {
                append(" (${widgetData.settledSchedule.timetableType.typeName})")
            }
        }.toString()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (widgetData?.settledSchedule != null && widgetData.reviewUiDto != null) {
                val header = when (widgetData.reviewUiDto.date) {
                    today -> {
                        "${glanceStringResource(R.string.today)}, " +
                                "${widgetData.reviewUiDto.date.format(dayMonthNameFormatter)}"
                    }

                    today.plusDays(1) -> {
                        "${glanceStringResource(R.string.tomorrow)}, " +
                                "${widgetData.reviewUiDto.date.format(dayMonthNameFormatter)}"
                    }

                    else -> {
                        widgetData.reviewUiDto.date.format(dayMonthNameFormatter)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
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

                            if (widgetData.reviewUiDto.currentWeek != -1) {
                                Spacer(modifier = GlanceModifier.width(4.dp))
                                Image(
                                    modifier = GlanceModifier.size(16.dp),
                                    provider = when (widgetData.reviewUiDto.currentWeek) {
                                        1 -> ImageProvider(R.drawable.one)
                                        2 -> ImageProvider(R.drawable.two)
                                        else -> ImageProvider(R.drawable.resource_null)
                                    },
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(
                                        colorProvider = GlanceTheme.colors.onBackground
                                    )
                                )
                            }
                        }

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
                val displayedEvents = widgetData.reviewUiDto.events.toList()

                if (displayedEvents.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    LazyColumn {
                        itemsIndexed(displayedEvents) { index, events ->
                            Column {
                                Event(
                                    events = events.second,
                                    eventsExtraData = widgetData.eventsExtraData,
                                    date = widgetData.reviewUiDto.date,
                                    eventExtraPolicy = widgetData.eventExtraPolicy
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
                        title = glanceStringResource(R.string.empty_day)
                    )
                }
            } else {
                Empty(
                    title = glanceStringResource(R.string.empty_here),
                    onClick = onUpdate
                )
            }
        }
    }

    @Composable
    private fun Event(
        events: List<Event>,
        eventsExtraData: Map<Long, EventExtraData>,
        date: LocalDate,
        eventExtraPolicy: EventExtraPolicy
    ) {
        Row(
            modifier = GlanceModifier
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
                    events.first().startDatetime.toLocalTimeWithTimeZone()
                }\n${
                    events.first().endDatetime.toLocalTimeWithTimeZone()
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
                modifier = GlanceModifier
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
                        eventExtraData = eventsExtraData.findEventExtra(
                            eventExtraPolicy = eventExtraPolicy,
                            eventId = event.id,
                            dateTime = event.startDatetime.replaceDate(date)
                        )
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
            text = event.name,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onBackground
            ),
            maxLines = 1
        )
        val rooms = event.rooms
        if (!rooms.isNullOrEmpty()) {
            EventComment(
                title = rooms.joinToString { it.name },
            ) {
                Image(
                    modifier = GlanceModifier.size(8.dp),
                    provider = ImageProvider(R.drawable.room),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        colorProvider = GlanceTheme.colors.onSecondaryContainer
                    )
                )
            }
        }
        if (eventExtraData != null && eventExtraData.comment.isNotBlank()) {
            EventComment(
                title = eventExtraData.comment.replace(Regex("\\s+"), " "),
                image = if (eventExtraData.tag != 0) {
                    {
                        val color = eventExtraData.tag.getColorByIndex()
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
                    }
                } else null
            )
        }
    }

    @Composable
    fun EventComment(
        title: String,
        image: (@Composable () -> Unit)?
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            image?.let {
                it.invoke()
                Spacer(modifier = GlanceModifier.width(4.dp))
            }
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = GlanceTheme.colors.onSecondaryContainer
                ),
                maxLines = 1
            )
        }
    }
}