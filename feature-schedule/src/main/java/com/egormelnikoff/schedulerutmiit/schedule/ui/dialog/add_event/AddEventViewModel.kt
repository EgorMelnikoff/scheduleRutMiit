package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.domain.DefaultEventParams
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.common.utils.extension.toUtcDateTime
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventActionUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.LoadEditEventUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime

@HiltViewModel(
    assistedFactory = AddEventViewModel.Factory::class
)
class AddEventViewModel @AssistedInject constructor(
    private val loadEditEventUseCase: LoadEditEventUseCase,
    private val eventActionUseCase: EventActionUseCase,
    private val appRouter: AppRouter,
    @Assisted
    private val eventId: Long?,
    @Assisted
    private val scheduleId: Long
) : BaseViewModel<AddEventState, AddEventAction>(AddEventState.Loading) {

    override fun onScreenInitialized() {
        super.onScreenInitialized()

        viewModelScope.launch {
            val initState = loadEditEventUseCase(eventId, scheduleId)
            updateState {
                initState
            }
        }
    }

    override fun onAction(action: AddEventAction) {
        when (action) {
            AddEventAction.OnAddGroup -> onAddGroup()
            AddEventAction.OnAddLecturer -> onAddLecturer()
            AddEventAction.OnAddRoom -> onAddRoom()
            AddEventAction.OnBackClicked -> onBackClicked()
            is AddEventAction.OnDateChanged -> updateForm {
                copy(date = action.date)
            }

            is AddEventAction.OnEditGroup -> onEditGroup(action.index, action.group)
            is AddEventAction.OnEditLecturer -> onEditLecturer(action.index, action.lecturer)
            is AddEventAction.OnEditRoom -> onEditRoom(action.index, action.room)
            is AddEventAction.OnStartTimeChanged -> updateForm {
                copy(startTime = action.time)
            }

            is AddEventAction.OnEndTimeChanged -> updateForm {
                copy(endTime = action.time)
            }

            is AddEventAction.OnIntervalChanged -> updateForm {
                copy(interval = action.interval)
            }

            is AddEventAction.OnPeriodChanged -> updateForm {
                copy(period = action.period)
            }

            is AddEventAction.OnNameChanged -> updateForm {
                copy(name = action.name)
            }

            is AddEventAction.OnRemoveGroup -> onRemoveGroup(action.index)
            is AddEventAction.OnRemoveLecturer -> onRemoveLecturer(action.index)
            is AddEventAction.OnRemoveRoom -> onRemoveRoom(action.index)
            AddEventAction.OnSaveClicked -> onSaveClicked()
            is AddEventAction.OnTypeChanged -> updateForm {
                copy(type = action.type)
            }

            is AddEventAction.OnShowDialogDate -> updateLoadedState {
                copy(showDialogDate = action.show)
            }

            is AddEventAction.OnShowDialogEndTime -> updateLoadedState {
                copy(showDialogEndTime = action.show)
            }

            is AddEventAction.OnShowDialogStartTime -> updateLoadedState {
                copy(showDialogStartTime = action.show)
            }

            is AddEventAction.OnShowBackDialog -> updateLoadedState {
                copy(showBackDialog = action.show)
            }
        }
    }

    private fun onBackClicked() {
        appRouter.onBack()
    }

    private fun onSaveClicked() {
        screenState.let {
            if (it is AddEventState.Loaded) {
                viewModelScope.launch {
                    it.editForm.date?.let { date ->
                        val startDateTime =
                            (it.editForm.startTime ?: LocalTime.of(0, 0)).toUtcDateTime(date)
                        val endDateTime =
                            (it.editForm.endTime ?: LocalTime.of(0, 0)).toUtcDateTime(date)

                        val event = Event(
                            id = eventId ?: 0,
                            scheduleId = scheduleId,
                            name = it.editForm.name.trim(),
                            typeName = it.editForm.type,

                            startDatetime = startDateTime,
                            endDatetime = endDateTime,
                            lecturers = it.editForm.lecturers,
                            rooms = it.editForm.rooms,
                            groups = it.editForm.groups,
                            isCustomEvent = true,
                            timeSlotName = getTimeSlotName(
                                startDateTime = startDateTime,
                                endDateTime = endDateTime
                            ),
                            interval = it.editForm.interval,
                            periodNumber = if (it.editForm.interval > 1) it.editForm.period else 1
                        )

                        if (it.editForm.isEdit) {
                            eventActionUseCase(EventAction.Update(event))
                        } else {
                            eventActionUseCase(EventAction.Add(event))
                        }
                    }
                }
            }
        }
    }

    private fun onAddRoom() {
        updateForm {
            copy(
                rooms = rooms + DefaultEventParams.defaultRoom
            )
        }
    }

    private fun onRemoveRoom(index: Int) {
        updateForm {
            copy(
                rooms = rooms.filterIndexed { i, _ ->
                    i != index
                }
            )
        }
    }

    private fun onEditRoom(index: Int, room: Room) {
        updateForm {
            copy(
                rooms = rooms.toMutableList().apply {
                    this[index] = room
                }
            )
        }
    }


    private fun onAddLecturer() {
        updateForm {
            copy(
                lecturers = lecturers + DefaultEventParams.defaultLecturer
            )
        }
    }

    private fun onRemoveLecturer(index: Int) {
        updateForm {
            copy(
                lecturers = lecturers.filterIndexed { i, _ ->
                    i != index
                }
            )
        }
    }

    private fun onEditLecturer(index: Int, lecturer: Lecturer) {
        updateForm {
            copy(
                lecturers = lecturers.toMutableList().apply {
                    this[index] = lecturer
                }
            )
        }
    }

    private fun onAddGroup() {
        updateForm {
            copy(
                groups = groups + DefaultEventParams.defaultGroup
            )
        }
    }

    private fun onRemoveGroup(index: Int) {
        updateForm {
            copy(
                groups = groups.filterIndexed { i, _ ->
                    i != index
                }
            )
        }
    }

    private fun onEditGroup(index: Int, group: Group) {
        updateForm {
            copy(
                groups = groups.toMutableList().apply {
                    this[index] = group
                }
            )
        }
    }

    private fun updateForm(update: EditForm.() -> EditForm) {
        updateLoadedState {
            copy(editForm = editForm.update())
        }
    }

    private fun updateLoadedState(update: AddEventState.Loaded.() -> AddEventState) {
        updateState {
            if (this is AddEventState.Loaded) this.update() else this
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long?, scheduleId: Long): AddEventViewModel
    }
}