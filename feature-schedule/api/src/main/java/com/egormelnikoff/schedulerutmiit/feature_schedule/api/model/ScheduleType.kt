package com.egormelnikoff.schedulerutmiit.feature_schedule.api.model

enum class ScheduleType(
    val typeName: String
) {
    GROUP("group"),
    PERSON("person"),
    ROOM("room"),
    MY("my")
}