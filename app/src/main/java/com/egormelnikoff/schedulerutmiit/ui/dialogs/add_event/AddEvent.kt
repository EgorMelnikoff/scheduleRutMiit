package com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.Group
import com.egormelnikoff.schedulerutmiit.app.model.Lecturer
import com.egormelnikoff.schedulerutmiit.app.model.RecurrenceRule
import com.egormelnikoff.schedulerutmiit.app.model.Room
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.app.model.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetTimePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButtonRow
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomChip
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.ListParam
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun AddEditEventDialog(
    editableEvent: Event? = null,
    appUiState: AppUiState,
    scheduleEntity: ScheduleEntity,
    navigationActions: NavigationActions,
    scheduleActions: ScheduleActions
) {
    var currentInterval by remember {
        mutableIntStateOf(
            editableEvent?.recurrenceRule?.interval ?: 1
        )
    }
    var currentPeriod by remember { mutableIntStateOf(editableEvent?.periodNumber ?: 1) }
    var showDialogDate by remember { mutableStateOf(false) }
    var showDialogStart by remember { mutableStateOf(false) }
    var showDialogEnd by remember { mutableStateOf(false) }

    var nameEvent by remember { mutableStateOf(editableEvent?.name ?: "") }
    var typeEvent by remember {
        mutableStateOf(
            editableEvent?.typeName ?: DefaultEventParams.types.first()
        )
    }
    var dateEvent by remember { mutableStateOf(editableEvent?.startDatetime?.toLocalDate()) }
    var startTime by remember { mutableStateOf(editableEvent?.startDatetime?.toLocaleTimeWithTimeZone()) }
    var endTime by remember { mutableStateOf(editableEvent?.endDatetime?.toLocaleTimeWithTimeZone()) }

    var roomsList by remember { mutableStateOf(editableEvent?.rooms ?: listOf()) }
    var lecturersList by remember { mutableStateOf(editableEvent?.lecturers ?: listOf()) }
    var groupsList by remember { mutableStateOf(editableEvent?.groups ?: listOf()) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = editableEvent?.let {
                    appUiState.context.getString(R.string.editing)
                } ?: appUiState.context.getString(R.string.adding_a_class),
                navAction = {
                    navigationActions.onBack()
                },
                actions = {
                    CustomButton(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        buttonTitle = editableEvent?.let {
                            appUiState.context.getString(R.string.save)
                        } ?: appUiState.context.getString(R.string.create),
                        onClick = {
                            val errorMessages = checkEventParams(
                                context = appUiState.context,
                                name = nameEvent,
                                date = dateEvent,
                                startTime = startTime,
                                endTime = endTime,
                                roomsList = roomsList,
                                lecturersList = lecturersList,
                                groupsList = groupsList
                            )
                            if (errorMessages.isEmpty()) {
                                val startDateTime = LocalDateTime.of(dateEvent, startTime)
                                    .atZone(ZoneId.systemDefault())
                                    .withZoneSameInstant(ZoneOffset.UTC)
                                    .toLocalDateTime()
                                val endDateTime = LocalDateTime.of(dateEvent, endTime)
                                    .atZone(ZoneId.systemDefault())
                                    .withZoneSameInstant(ZoneOffset.UTC)
                                    .toLocalDateTime()

                                val event = Event(
                                    id = editableEvent?.id ?: 0,
                                    scheduleId = scheduleEntity.id,
                                    name = nameEvent.trim(),
                                    typeName = typeEvent,

                                    startDatetime = startDateTime,
                                    endDatetime = endDateTime,
                                    lecturers = lecturersList,
                                    rooms = roomsList,
                                    groups = groupsList,
                                    isCustomEvent = true,
                                    timeSlotName = getTimeSlotName(
                                        startDateTime = startDateTime,
                                        endDateTime = endDateTime
                                    ),
                                    recurrenceRule = scheduleEntity.recurrence?.let {
                                        RecurrenceRule(
                                            frequency = "WEEKLY",
                                            interval = currentInterval
                                        )
                                    },
                                    periodNumber = if (currentInterval > 1) currentPeriod else 1
                                )
                                editableEvent?.let {
                                    if (editableEvent != event) {
                                        scheduleActions.eventActions.onUpdateCustomEvent(
                                            scheduleEntity,
                                            event
                                        )
                                    }
                                } ?: scheduleActions.eventActions.onAddCustomEvent(
                                    scheduleEntity, event
                                )
                                navigationActions.onBack()
                            } else {
                                appUiState.scope.launch {
                                    appUiState.snackBarHostState.showSnackbar(
                                        message = errorMessages,
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            }
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 16.dp, end = 16.dp,
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = nameEvent,
                placeholderText = stringResource(R.string.class_name),
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done
                )
            ) { newValue ->
                nameEvent = newValue
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
                            CustomChip(
                                title = type
                                    ?: stringResource(R.string.not_specified),
                                imageVector = null,
                                selected = type == typeEvent,
                                onSelect = {
                                    typeEvent = type
                                    appUiState.focusManager.clearFocus()
                                }
                            )
                        }
                    }
                }
            )

            scheduleEntity.recurrence?.let {
                DateSelector(
                    dateEvent = dateEvent,
                    onSelectDateEvent = { value ->
                        dateEvent = value
                    },
                    focusManager = appUiState.focusManager
                )
                TimeSelector(
                    focusManager = appUiState.focusManager,
                    startTime = startTime,
                    endTime = endTime,
                    onShowDialogStartTime = { value ->
                        showDialogStart = value
                    },
                    onShowDialogEndTime = { value ->
                        showDialogEnd = value
                    }
                )
                RecurrenceField(
                    maxInterval = scheduleEntity.recurrence.interval!!,
                    currentInterval = currentInterval,
                    currentPeriod = currentPeriod,
                    onSelectInterval = { value ->
                        currentInterval = value
                    },
                    onSelectPeriod = { value ->
                        currentPeriod = value
                    },
                )
            } ?: DateTimeSelector(
                focusManager = appUiState.focusManager,
                dateEvent = dateEvent,
                startTime = startTime,
                endTime = endTime,
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

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline
            )
            ListParam(
                title = stringResource(R.string.room),
                elements = roomsList,
                onAddElement = {
                    roomsList = (roomsList + DefaultEventParams.defaultRoom)
                    appUiState.focusManager.clearFocus()
                },
                maxCount = 1
            ) { index, room ->
                RoomInput(
                    room = room,
                    onValueChanged = { updatedRoom ->
                        roomsList =
                            roomsList.toMutableList().apply { this[index] = updatedRoom }
                    },
                    onRemove = {
                        roomsList = roomsList.toMutableList().apply { removeAt(index) }
                    }
                )
            }
            ListParam(
                title = appUiState.context.getString(R.string.lecturers),
                elements = lecturersList,
                onAddElement = {
                    lecturersList = (lecturersList + DefaultEventParams.defaultLecturer)
                    appUiState.focusManager.clearFocus()
                },
                maxCount = 3
            ) { index, lecturer ->
                LecturerInput(
                    lecturer = lecturer,
                    onValueChanged = { updatedLecturer ->
                        lecturersList =
                            lecturersList.toMutableList()
                                .apply { this[index] = updatedLecturer }
                    },
                    onRemove = {
                        lecturersList =
                            lecturersList.toMutableList().apply { removeAt(index) }
                    }
                )
            }
            ListParam(
                title = stringResource(R.string.groups),
                elements = groupsList,
                onAddElement = {
                    groupsList = groupsList + DefaultEventParams.defaultGroup
                    appUiState.focusManager.clearFocus()
                },
                maxCount = 7
            ) { index, group ->
                GroupInput(
                    group = group,
                    onValueChanged = { updatedGroup ->
                        groupsList =
                            groupsList.toMutableList().apply { this[index] = updatedGroup }
                    },
                    onRemove = {
                        groupsList = groupsList.toMutableList().apply { removeAt(index) }
                    }
                )
            }
        }
        if (showDialogDate) {
            BottomSheetDatePicker(
                selectedDate = dateEvent,
                onDateSelect = { newValue -> dateEvent = newValue },
                onShowDialog = { newValue -> showDialogDate = newValue },
                startDate = scheduleEntity.startDate,
                endDate = scheduleEntity.endDate
            )
        }
        if (showDialogStart) {
            BottomSheetTimePicker(
                selectedTime = startTime,
                onTimeSelect = { newValue ->
                    startTime = newValue
                    if (endTime == null) {
                        endTime = newValue
                            .plusHours(1)
                            .plusMinutes(20)
                    }
                },
                onShowDialog = { newValue -> showDialogStart = newValue },
            )
        }
        if (showDialogEnd) {
            BottomSheetTimePicker(
                selectedTime = endTime,
                onTimeSelect = { newValue ->
                    endTime = newValue
                },
                onShowDialog = { newValue -> showDialogEnd = newValue }
            )
        }
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
            enter = expandVertically(),
            exit = shrinkVertically()
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

fun checkEventParams(
    context: Context,
    name: String,
    date: LocalDate?,
    startTime: LocalTime?,
    endTime: LocalTime?,
    roomsList: List<Room>,
    lecturersList: List<Lecturer>,
    groupsList: List<Group>
): String {
    val errorString = StringBuilder().apply {
        if (name.isEmpty()) {
            append("${context.getString(R.string.no_name_specified)}\n")
        }
        if (date == null) {
            append("${context.getString(R.string.no_date_specified)}\n")
        }
        if (startTime == null) {
            append("${context.getString(R.string.no_start_time_specified)}\n")
        }
        if (endTime == null) {
            append("${context.getString(R.string.no_end_time_specified)}\n")
        }
        if (startTime != null && endTime != null && startTime > endTime) {
            append("${context.getString(R.string.time_is_chosen_incorrectly)}\n")
        }

        if (!roomsList.all { it.name!!.isNotEmpty() }) {
            append("${context.getString(R.string.room_number_not_specified)}\n")
        }
        if (!lecturersList.all { it.fullFio!!.isNotEmpty() }) {
            append("${context.getString(R.string.provide_the_names_of_all_lecturers)}\n")
        }
        if (!groupsList.all { it.name!!.isNotEmpty() }) {
            append("${context.getString(R.string.provide_the_numbers_of_all_groups)}\n")
        }
    }.trimEnd()
    return errorString.toString()
}