package com.egormelnikoff.schedulerutmiit.app.enums_sealed


enum class TimetableType(
    val typeName: String,
    val id: Int
) {
    PERIODIC("Периодическое", 1), NON_PERIODIC("Разовое", 2), SESSION("Сессия", 4)
}