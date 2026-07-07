package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.schedule.domain.manager.ScheduleManager
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.AddCustomNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.view_model.state.AddScheduleForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddScheduleViewModel @Inject constructor(
    private val addCustomNamedScheduleUseCase: AddCustomNamedScheduleUseCase,
    private val scheduleManager: ScheduleManager
) : ViewModel() {
    private val _form = MutableStateFlow(AddScheduleForm())
    val form = _form.asStateFlow()

    fun updateName(name: String) {
        updateForm {
            copy(
                name = name
            )
        }
    }

    fun updateDates(
        start: LocalDate?,
        end: LocalDate?
    ) {
        updateForm {
            copy(
                startDate = start,
                endDate = end
            )
        }
    }

    fun updateTimetableType(type: TimetableType) {
        updateForm {
            copy(
                timetableType = type
            )
        }
    }

    private fun updateForm(
        update: AddScheduleForm.() -> AddScheduleForm
    ) {
        val form = _form.value
        _form.value = form.update()
    }


    fun addCustomNamedSchedule() {
        val form = _form.value

        if (!form.isButtonEnabled) return

        viewModelScope.launch {
            val result = addCustomNamedScheduleUseCase(
                form.name.trim(),
                form.startDate!!,
                form.endDate!!,
                form.timetableType
            )

            if (result.second) {
                scheduleManager.openDefault()
            } else {
                scheduleManager.openSaved(result.first)
            }
        }
    }
}