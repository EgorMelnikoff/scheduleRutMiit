package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.time.TimeProvider
import com.egormelnikoff.schedulerutmiit.schedule.domain.manager.ScheduleManager
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.SetDefaultNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.observer.ObserveNamedSchedulesUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.observer.ObserveSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    observeNamedSchedulesUseCase: ObserveNamedSchedulesUseCase,
    observeSummaryUseCase: ObserveSummaryUseCase,
    timeProvider: TimeProvider,

    private val setDefaultNamedScheduleUseCase: SetDefaultNamedScheduleUseCase,
    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,

    private val scheduleManager: ScheduleManager
) : ViewModel() {
    val namedSchedules = observeNamedSchedulesUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    val summaryState = combine(
        observeSummaryUseCase(),
        timeProvider.currentHour
    ) { summaryState, _ ->
        summaryState
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val currentDateTime = timeProvider.currentHour
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        )

    fun setNamedSchedule(
        namedScheduleId: Long,
        setDefault: Boolean = false
    ) {
        viewModelScope.launch {
            if (setDefault) {
                setDefaultNamedScheduleUseCase(namedScheduleId)
                scheduleManager.openDefault()
            } else {
                scheduleManager.openSaved(namedScheduleId)
            }
        }
    }

    fun deleteNamedSchedule(
        namedScheduleId: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            scheduleManager.openDefault()

            deleteNamedScheduleUseCase(
                namedScheduleId, isDefault
            )
        }
    }
}