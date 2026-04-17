package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Keep
data class SchedulePagerUiDto(
    val today: LocalDate,
    val defaultDate: LocalDate,
    val weeksCount: Int,
    val weeksStartIndex: Int,
    val daysCount: Int,
    val daysStartIndex: Int,
) {
    companion object {
        operator fun invoke(
            today: LocalDate,
            startDate: LocalDate,
            endDate: LocalDate
        ): SchedulePagerUiDto {
            val weeksCount = ChronoUnit.WEEKS.between(
                startDate.getFirstDayOfWeek(),
                endDate.getFirstDayOfWeek()
            ).plus(1).toInt()

            val daysCount = ChronoUnit.DAYS.between(
                startDate,
                endDate
            ).plus(1).toInt()

            val defaultDate: LocalDate
            val weeksStartIndex: Int
            val daysStartIndex: Int

            if (today in startDate..endDate) {
                weeksStartIndex = abs(
                    ChronoUnit.WEEKS.between(
                        startDate.getFirstDayOfWeek(),
                        today.getFirstDayOfWeek()
                    ).toInt()
                )
                daysStartIndex = abs(
                    ChronoUnit.DAYS.between(
                        startDate,
                        today
                    ).toInt()
                )
                defaultDate = today
            } else if (today < startDate) {
                weeksStartIndex = 0
                daysStartIndex = 0
                defaultDate = startDate
            } else {
                weeksStartIndex = weeksCount
                daysStartIndex = weeksCount * 7
                defaultDate = endDate
            }
            return SchedulePagerUiDto(
                today = today,
                defaultDate = defaultDate,
                weeksCount = weeksCount,
                weeksStartIndex = weeksStartIndex,
                daysCount = daysCount,
                daysStartIndex = daysStartIndex
            )
        }
    }
}