package com.egormelnikoff.schedulerutmiit.feature_schedule.api.model

enum class NamedScheduleType(
    val typeName: String
) {
    GROUP("group"),
    PERSON("person"),
    ROOM("room"),
    MY("my")
}