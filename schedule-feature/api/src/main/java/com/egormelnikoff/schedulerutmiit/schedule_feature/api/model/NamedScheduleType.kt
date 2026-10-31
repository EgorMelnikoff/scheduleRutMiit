package com.egormelnikoff.schedulerutmiit.schedule_feature.api.model

enum class NamedScheduleType(
    val typeName: String
) {
    GROUP("group"),
    PERSON("person"),
    ROOM("room"),
    MY("my")
}