package com.egormelnikoff.schedulerutmiit.feature_schedule.glance.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetDataEventItem(
    val name: String,
    val type: String?,
    val room: String? = null,
    val comment: String? = null
)