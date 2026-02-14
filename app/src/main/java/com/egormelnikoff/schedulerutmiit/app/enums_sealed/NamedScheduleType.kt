package com.egormelnikoff.schedulerutmiit.app.enums_sealed

enum class NamedScheduleType(
    val id: Int,
    val typeName: String
) {
    GROUP(0, "group"),
    PERSON(1, "person"),
    ROOM(2, "room"),
    MY(3, "my")
}