package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class CalendarState(
    val calendarData: CalendarData,
    private val pagerWeeksState: PagerState,
    private val pagerDaysState: PagerState,
    private val scope: CoroutineScope
) {
    var selectedDate by mutableStateOf(calendarData.initialDate)
        private set

    val currentWeekPage: Int
        get() = pagerWeeksState.currentPage

    fun getPagerDays() = pagerDaysState

    fun getPagerWeeks() = pagerWeeksState

    private val scrollMutex = Mutex()

    fun selectDate(date: LocalDate, animate: Boolean = true) {
        val targetWeekPage = getTargetWeekIndex(calendarData.startDate, date)

        if (selectedDate != date) {
            selectedDate = date
        } else if (targetWeekPage == pagerWeeksState.currentPage) {
            return
        }

        scope.launch {
            synchronizedScroll {
                val targetDayPage = ChronoUnit.DAYS.between(calendarData.startDate, date).toInt()

                if (animate) {
                    coroutineScope {
                        val jobDays = async { pagerDaysState.animateScrollToPage(targetDayPage) }
                        val jobWeeks = async { pagerWeeksState.animateScrollToPage(targetWeekPage) }

                        jobDays.await()
                        jobWeeks.await()
                    }
                } else {
                    pagerDaysState.scrollToPage(targetDayPage)
                    pagerWeeksState.scrollToPage(targetWeekPage)
                }
            }
        }
    }

    suspend fun selectDateFromPager(dayPage: Int) = synchronizedScroll {
        val newDate = calendarData.startDate.plusDays(dayPage.toLong())

        if (selectedDate != newDate) {
            selectedDate = newDate

            val targetWeekIndex = getTargetWeekIndex(calendarData.startDate, newDate)

            if (pagerWeeksState.targetPage != targetWeekIndex) {
                pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }
    }

    fun scrollWeek(index: Int, animate: Boolean = true) {
        scope.launch {
            if (animate) pagerWeeksState.animateScrollToPage(index)
            else pagerWeeksState.scrollToPage(index)
        }
    }

    fun scrollWeekForward(animate: Boolean = true) {
        scrollWeek(pagerWeeksState.currentPage + 1, animate)
    }

    fun scrollWeekBackward(animate: Boolean = true) {
        scrollWeek(pagerWeeksState.currentPage - 1, animate)
    }

    private fun getTargetWeekIndex(
        startDate: LocalDate,
        targetDate: LocalDate
    ) = ChronoUnit.WEEKS
        .between(
            startDate.getFirstDayOfWeek(),
            targetDate.getFirstDayOfWeek()
        )
        .toInt()

    private suspend fun synchronizedScroll(block: suspend () -> Unit) {
        if (!scrollMutex.tryLock()) return
        try {
            block()
        } finally {
            scrollMutex.unlock()
        }
    }
}


@Composable
fun rememberCalendarState(
    calendarData: CalendarData = remember { CalendarData() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): CalendarState {
    val (pagerDaysState, pagerWeeksState) = key(calendarData) {
        val daysState = rememberPagerState(
            pageCount = { calendarData.daysCount },
            initialPage = calendarData.daysPagerInitialIndex
        )
        val weeksState = rememberPagerState(
            pageCount = { calendarData.weeksCount },
            initialPage = calendarData.weeksPagerInitialIndex
        )
        Pair(daysState, weeksState)
    }

    val calendarState = remember(
        calendarData
    ) {
        CalendarState(
            calendarData = calendarData,
            pagerWeeksState = pagerWeeksState,
            pagerDaysState = pagerDaysState,
            scope = coroutineScope
        )
    }

    LaunchedEffect(pagerDaysState.currentPage) {
        calendarState.selectDateFromPager(pagerDaysState.currentPage)
    }

    return calendarState
}