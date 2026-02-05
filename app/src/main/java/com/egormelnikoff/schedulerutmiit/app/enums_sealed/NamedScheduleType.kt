package com.egormelnikoff.schedulerutmiit.app.enums_sealed

sealed class NamedScheduleType(
    val id: Int,
    val name: String
) {
    data object Group : NamedScheduleType(
        0, "group"
    )

    data object Person : NamedScheduleType(
        1, "person"
    )

    data object Room : NamedScheduleType(
        2, "room"
    )

    data object My : NamedScheduleType(
        3, "my"
    )
}