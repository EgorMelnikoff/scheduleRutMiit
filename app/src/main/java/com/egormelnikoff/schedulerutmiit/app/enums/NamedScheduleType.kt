package com.egormelnikoff.schedulerutmiit.app.enums

import androidx.annotation.Keep

@Keep
enum class NamedScheduleType(
    val typeName: String
) {
    GROUP("group"),
    PERSON("person"),
    ROOM("room"),
    MY("my")
}