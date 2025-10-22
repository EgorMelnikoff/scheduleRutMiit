package com.egormelnikoff.schedulerutmiit.app.widget.receivers

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget

class EventsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EventsWidget()
}