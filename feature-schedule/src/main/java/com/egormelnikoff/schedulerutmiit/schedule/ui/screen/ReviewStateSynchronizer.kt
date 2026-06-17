package com.egormelnikoff.schedulerutmiit.schedule.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import java.time.LocalDateTime

@Composable
fun ReviewStateSynchronizer(
    hourlyDateTime: LocalDateTime,
    scheduleViewModel: ScheduleViewModel,
) {
    LaunchedEffect(hourlyDateTime) {
        scheduleViewModel.refreshReview()
    }
}
