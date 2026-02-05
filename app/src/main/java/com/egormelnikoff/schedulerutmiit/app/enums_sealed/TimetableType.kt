package com.egormelnikoff.schedulerutmiit.app.enums_sealed


enum class TimetableType(
    val typeName: String
) {
    PERIODIC("Периодическое"), NON_PERIODIC("Разовое"), SESSION("Сессия")
}