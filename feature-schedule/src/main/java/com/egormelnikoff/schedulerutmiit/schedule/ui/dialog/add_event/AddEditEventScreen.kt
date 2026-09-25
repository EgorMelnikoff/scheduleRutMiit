package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.DefaultEventParams
import com.egormelnikoff.schedulerutmiit.core.common.extension.toUtcDateTime
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButtonRow
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomFilterChip
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.DateTimeSelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.DaySelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.GroupInput
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LecturerInput
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ListParam
import com.egormelnikoff.schedulerutmiit.core.ui.elements.PagerScreenContainer
import com.egormelnikoff.schedulerutmiit.core.ui.elements.RoomInput
import com.egormelnikoff.schedulerutmiit.core.ui.elements.TimeSelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.view_model.EditEventViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.view_model.state.EditEventState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventDialog(
    editEventDialog: Route.Dialog.EditEventDialog,
    onBack: () -> Unit
) {
    val editEventViewModel =
        hiltViewModel<EditEventViewModel, EditEventViewModel.Factory> { factory ->
            factory.create(editEventDialog.eventId, editEventDialog.scheduleId)
        }
    val editEventState = editEventViewModel.editEventState.collectAsStateWithLifecycle().value
    val form = editEventViewModel.form.collectAsStateWithLifecycle().value

    when (editEventState) {
        is EditEventState.Loaded if form != null -> {
            var showBackDialog by remember { mutableStateOf(false) }

            BackHandler {
                showBackDialog = true
            }

            val focusManager = LocalFocusManager.current

            var showDialogDate by remember { mutableStateOf(false) }
            var showDialogStart by remember { mutableStateOf(false) }
            var showDialogEnd by remember { mutableStateOf(false) }


            Scaffold(
                topBar = {
                    CustomTopAppBar(
                        titleText = if (form.isEdit) stringResource(R.string.editing)
                        else stringResource(R.string.create_class),
                        navAction = onBack
                    )
                }
            ) { innerPadding ->
                val pagerState = rememberPagerState(
                    initialPage = 0
                ) { 3 }

                PagerScreenContainer(
                    pagerState = pagerState,
                    isNextEnabled = { page ->
                        when (page) {
                            0 -> form.name.isNotBlank()
                            1 -> form.date != null && form.startTime != null && form.endTime != null && (editEventState.schedule.recurrence != null && form.interval != -1 || editEventState.schedule.recurrence == null)
                            2 -> form.isValid
                            else -> true
                        }
                    },
                    onFinish = {
                        form.date?.let {
                            val startDateTime = form.startTime?.toUtcDateTime(it)
                            val endDateTime = form.endTime?.toUtcDateTime(it)
                            if (startDateTime != null && endDateTime != null) {
                                editEventViewModel.eventAction()
                            }
                        }
                        onBack()
                    },
                    paddingValues = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = innerPadding.calculateTopPadding() + 12.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                    finishTitle = if (form.isEdit) {
                        stringResource(R.string.save)
                    } else {
                        stringResource(R.string.create)
                    }
                ) { page ->
                    when (page) {
                        0 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CustomTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = form.name,
                                    placeholderText = stringResource(R.string.class_name),
                                    keyboardOptions = KeyboardOptions(
                                        autoCorrectEnabled = false,
                                        imeAction = ImeAction.Done
                                    )
                                ) { newValue ->
                                    editEventViewModel.onNameChanged(newValue)
                                }
                                ColumnGroup(
                                    title = stringResource(R.string.class_type),
                                    withBackground = false,
                                    items = listOf {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(
                                                8.dp,
                                                Alignment.CenterVertically
                                            )
                                        ) {
                                            DefaultEventParams.types.forEach { type ->
                                                CustomFilterChip(
                                                    title = type
                                                        ?: stringResource(R.string.not_specified),
                                                    imageVector = null,
                                                    selected = type == form.type,
                                                    onClick = {
                                                        editEventViewModel.onTypeChanged(type)
                                                        focusManager.clearFocus()
                                                    }
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        1 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                            ) {
                                if (editEventState.schedule.recurrence != null) {
                                    DaySelector(
                                        dateEvent = form.date,
                                        onSelectDateEvent = { date ->
                                            editEventViewModel.onDateChanged(date)
                                        },
                                        focusManager = focusManager
                                    )
                                    TimeSelector(
                                        focusManager = focusManager,
                                        startTime = form.startTime,
                                        endTime = form.endTime,
                                        onShowDialogStartTime = { value ->
                                            showDialogStart = value
                                        },
                                        onShowDialogEndTime = { value ->
                                            showDialogEnd = value
                                        }
                                    )
                                    RecurrenceField(
                                        maxInterval = editEventState.schedule.recurrence!!.interval,
                                        currentInterval = form.interval,
                                        currentPeriod = form.period,
                                        onSelectInterval = { value ->
                                            editEventViewModel.onIntervalChanged(value)
                                        },
                                        onSelectPeriod = { value ->
                                            editEventViewModel.onPeriodChanged(value)
                                        },
                                    )
                                } else {
                                    DateTimeSelector(
                                        focusManager = focusManager,
                                        dateEvent = form.date,
                                        startTime = form.startTime,
                                        endTime = form.endTime,
                                        onShowDialogDate = { value ->
                                            showDialogDate = value
                                        },
                                        onShowDialogStartTime = { value ->
                                            showDialogStart = value
                                        },
                                        onShowDialogEndTime = { value ->
                                            showDialogEnd = value
                                        }
                                    )
                                }
                            }
                        }

                        2 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                            ) {
                                ListParam(
                                    title = stringResource(R.string.room),
                                    elements = form.rooms,
                                    onAddElement = {
                                        editEventViewModel.addRoom()
                                        focusManager.clearFocus()
                                    },
                                    maxCount = 1
                                ) { index, room ->
                                    RoomInput(
                                        room = room,
                                        onValueChanged = { updatedRoom ->
                                            editEventViewModel.onRoomChanged(index, updatedRoom)
                                        },
                                        onRemove = {
                                            editEventViewModel.removeRoom(index)
                                        }
                                    )
                                }
                                ListParam(
                                    title = stringResource(R.string.lecturers),
                                    elements = form.lecturers,
                                    onAddElement = {
                                        editEventViewModel.addLecturer()
                                        focusManager.clearFocus()
                                    },
                                    maxCount = 3
                                ) { index, lecturer ->
                                    LecturerInput(
                                        lecturer = lecturer,
                                        onValueChanged = { updatedLecturer ->
                                            editEventViewModel.onLecturerChanged(
                                                index,
                                                updatedLecturer
                                            )
                                        },
                                        onRemove = {
                                            editEventViewModel.removeLecturer(index)
                                        }
                                    )
                                }
                                ListParam(
                                    title = stringResource(R.string.groups),
                                    elements = form.groups,
                                    onAddElement = {
                                        editEventViewModel.addGroup()
                                        focusManager.clearFocus()
                                    },
                                    maxCount = 7
                                ) { index, group ->
                                    GroupInput(
                                        group = group,
                                        onValueChanged = { updatedGroup ->
                                            editEventViewModel.onGroupChanged(index, updatedGroup)
                                        },
                                        onRemove = {
                                            editEventViewModel.removeGroup(index)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (showDialogDate) {
                    BottomSheetDatePicker(
                        selectedDate = form.date,
                        onDateSelect = { newValue -> editEventViewModel.onDateChanged(newValue) },
                        onShowDialog = { newValue -> showDialogDate = newValue },
                        startDate = editEventState.schedule.startDate,
                        endDate = editEventState.schedule.endDate
                    )
                }
                if (showDialogStart) {
                    BottomSheetTimePicker(
                        selectedTime = form.startTime,
                        onTimeSelect = { newValue ->
                            editEventViewModel.onStartTimeChanged(newValue)
                            if (form.startTime == null) {
                                editEventViewModel.onEndTimeChanged(
                                    newValue
                                        .plusHours(1)
                                        .plusMinutes(20)
                                )
                            }
                        },
                        onShowDialog = { newValue -> showDialogStart = newValue },
                    )
                }
                if (showDialogEnd) {
                    BottomSheetTimePicker(
                        selectedTime = form.endTime,
                        onTimeSelect = { newValue ->
                            editEventViewModel.onEndTimeChanged(newValue)
                        },
                        onShowDialog = { newValue -> showDialogEnd = newValue }
                    )
                }

                if (showBackDialog) {
                    CustomAlertDialog(
                        dialogTitle = stringResource(R.string.exit),
                        dialogText = stringResource(R.string.exit_message),
                        onDismissRequest = {
                            showBackDialog = false
                        },
                        onConfirmation = onBack
                    )
                }
            }
        }
        else -> LoadingScreen()
    }
}

@Composable
fun RecurrenceField(
    maxInterval: Int,
    currentInterval: Int,
    currentPeriod: Int,
    onSelectInterval: (Int) -> Unit,
    onSelectPeriod: (Int) -> Unit
) {
    Column {
        ColumnGroup(
            title = stringResource(R.string.repetition),
            withBackground = false,
            items = listOf {
                CustomButtonRow(
                    selectedElement = currentInterval,
                    elements = (1..maxInterval).toList(),
                    onClick = { element ->
                        onSelectInterval(element)
                    },
                    label = { element ->
                        Text(
                            text = if (element.second == 1) {
                                stringResource(R.string.every_week)
                            } else {
                                stringResource(R.string.once_week, element.second)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(
            visible = currentInterval > 1,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                ColumnGroup(
                    title = stringResource(R.string.week, ""),
                    withBackground = false,
                    items = listOf {
                        CustomButtonRow(
                            selectedElement = currentPeriod,
                            elements = (1..maxInterval).toList(),
                            onClick = { element ->
                                onSelectPeriod(element)
                            },
                            label = { element ->
                                Text(
                                    text = element.second.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}