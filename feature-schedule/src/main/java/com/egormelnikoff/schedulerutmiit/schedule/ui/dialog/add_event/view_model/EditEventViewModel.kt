package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.domain.DefaultEventParams
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.common.extension.toUtcDateTime
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.LoadEditEventUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.LoadEventUseCase
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.view_model.state.EditEventForm
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.view_model.state.EditEventState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@HiltViewModel(
    assistedFactory = EditEventViewModel.Factory::class
)
class EditEventViewModel @AssistedInject constructor(
    private val loadEventUseCase: LoadEventUseCase,
    private val loadEditEventUseCase: LoadEditEventUseCase,
    private val eventActionUseCase: EventActionUseCase,
    @Assisted
    private val eventId: Long?,
    @Assisted
    private val scheduleId: Long
) : ViewModel() {
    private val _editEventState = MutableStateFlow<EditEventState>(EditEventState.Loading)
    val editEventState = _editEventState.asStateFlow()

    private val _form = MutableStateFlow<EditEventForm?>(null)
    val form = _form.asStateFlow()

    init {
        viewModelScope.launch {
            _editEventState.value = loadEditEventUseCase(eventId, scheduleId)
            _form.value = EditEventForm(
                eventId?.let {
                    loadEventUseCase(it, null).event
                }
            )
        }
    }

    fun eventAction() {
        _form.value?.let {
            viewModelScope.launch {
                it.date?.let { date ->
                    val startDateTime = (it.startTime ?: LocalTime.of(0, 0)).toUtcDateTime(date)
                    val endDateTime = (it.endTime ?: LocalTime.of(0, 0)).toUtcDateTime(date)

                    val event = Event(
                        id = eventId ?: 0,
                        scheduleId = scheduleId,
                        name = it.name.trim(),
                        typeName = it.type,

                        startDatetime = startDateTime,
                        endDatetime = endDateTime,
                        lecturers = it.lecturers,
                        rooms = it.rooms,
                        groups = it.groups,
                        isCustomEvent = true,
                        timeSlotName = getTimeSlotName(
                            startDateTime = startDateTime,
                            endDateTime = endDateTime
                        ),
                        interval = it.interval,
                        periodNumber = if (it.interval > 1) it.period else 1
                    )

                    if (it.isEdit) {
                        eventActionUseCase(EventAction.Update(event))
                    } else {
                        eventActionUseCase(EventAction.Add(event))
                    }
                }
            }
        }
    }

    fun onNameChanged(value: String) {
        updateForm {
            copy(name = value)
        }
    }

    fun onTypeChanged(type: String?) {
        updateForm {
            copy(type = type)
        }
    }

    fun onIntervalChanged(interval: Int) {
        updateForm {
            copy(interval = interval)
        }
    }

    fun onPeriodChanged(period: Int) {
        updateForm {
            copy(period = period)
        }
    }

    fun onDateChanged(date: LocalDate) {
        updateForm {
            copy(date = date)
        }
    }

    fun onStartTimeChanged(startTime: LocalTime) {
        updateForm {
            copy(startTime = startTime)
        }
    }

    fun onEndTimeChanged(endTime: LocalTime) {
        updateForm {
            copy(endTime = endTime)
        }
    }

    fun addRoom() {
        updateForm {
            copy(
                rooms = rooms + DefaultEventParams.defaultRoom
            )
        }
    }

    fun removeRoom(index: Int) {
        updateForm {
            copy(
                rooms = rooms.filterIndexed { i, _ ->
                    i != index
                }
            )
        }
    }

    fun onRoomChanged(index: Int, room: Room) {
        updateForm {
            copy(
                rooms = rooms.toMutableList().apply {
                    this[index] = room
                }
            )
        }
    }


    fun addLecturer() {
        updateForm {
            copy(
                lecturers = lecturers + DefaultEventParams.defaultLecturer
            )
        }
    }

    fun removeLecturer(index: Int) {
        updateForm {
            copy(
                lecturers = lecturers.filterIndexed { i, _ ->
                    i != index
                }
            )
        }
    }

    fun onLecturerChanged(index: Int, lecturer: Lecturer) {
        updateForm {
            copy(
                lecturers = lecturers.toMutableList().apply {
                    this[index] = lecturer
                }
            )
        }
    }

    fun addGroup() {
        updateForm {
            copy(
                groups = groups + DefaultEventParams.defaultGroup
            )
        }
    }

    fun removeGroup(index: Int) {
        updateForm {
            copy(
                groups = groups.filterIndexed { i, _ ->
                    i != index
                }
            )
        }
    }

    fun onGroupChanged(index: Int, group: Group) {
        updateForm {
            copy(
                groups = groups.toMutableList().apply {
                    this[index] = group
                }
            )
        }
    }

    private fun updateForm(
        update: EditEventForm.() -> EditEventForm
    ) {
        val form = _form.value
        _form.value = form?.update()
    }

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long?, scheduleId: Long): EditEventViewModel
    }
}