package com.egormelnikoff.schedulerutmiit.schedule_feature.api.model

enum class TimetableType(
    val typeName: String,
    val id: Int
) {
    PERIODIC("Периодическое", 1), NON_PERIODIC("Разовое", 2), SESSION("Сессия", 4);

    fun isPeriodic() = this == PERIODIC
}