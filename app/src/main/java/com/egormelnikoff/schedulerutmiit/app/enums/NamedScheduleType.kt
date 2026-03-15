package com.egormelnikoff.schedulerutmiit.app.enums

import androidx.annotation.Keep

@Keep
enum class NamedScheduleType(
    val id: Int,
    val typeName: String
) {
    GROUP(0, "group"),
    PERSON(1, "person"),
    ROOM(2, "room"),
    MY(3, "my")
}