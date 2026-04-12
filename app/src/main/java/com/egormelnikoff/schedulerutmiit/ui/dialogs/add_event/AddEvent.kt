package com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.RecurrenceEventDto
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.app.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.app.validator.EventValidation
import com.egormelnikoff.schedulerutmiit.domain.schedule.EventAction
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetTimePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButtonRow
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomFilterChip
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.ListParam
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun AddEditEventDialog(
    namedScheduleEntity: NamedScheduleEntity,
    scheduleEntity: ScheduleEntity,
    editableEvent: Event? = null,
    appUiState: AppUiState,
    currentDateTime: LocalDateTime,

    scheduleViewModel: ScheduleViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

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
    var startTime by remember { mutableStateOf(editableEvent?.startDatetime?.toLocalTimeWithTimeZone()) }
    var endTime by remember { mutableStateOf(editableEvent?.endDatetime?.toLocalTimeWithTimeZone()) }

    var roomsList by remember { mutableStateOf(editableEvent?.rooms ?: listOf()) }
    var lecturersList by remember { mutableStateOf(editableEvent?.lecturers ?: listOf()) }
    var groupsList by remember { mutableStateOf(editableEvent?.groups ?: listOf()) }

    val buttonEnabled by remember {
        derivedStateOf {
            EventValidation.validate(
                context = context,
                name = nameEvent,
                date = dateEvent,
                startTime = startTime,
                endTime = endTime,
                roomsList = roomsList,
                lecturersList = lecturersList,
                groupsList = groupsList
            ) is EventValidation.Success
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = editableEvent?.let {
                    stringResource(R.string.editing)
                } ?: stringResource(R.string.adding_a_class),
                subtitleText = "${namedScheduleEntity.shortName} (${scheduleEntity.timetableType.typeName})",
                navAction = {
                    appUiState.appBackStack.onBack()
                },
                actions = {
                    CustomButton(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        buttonTitle = editableEvent?.let {
                            stringResource(R.string.save)
                        } ?: stringResource(R.string.create),
                        enabled = buttonEnabled,
                        onClick = {
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
                                    RecurrenceEventDto(
                                        frequency = "WEEKLY",
                                        interval = currentInterval
                                    )
                                },
                                periodNumber = if (currentInterval > 1) currentPeriod else 1
                            )
                            editableEvent?.let {
                                if (editableEvent != event) {
                                    scheduleViewModel.eventAction(scheduleEntity, event, EventAction.Update)
                                }
                            } ?: scheduleViewModel.eventAction(
                                scheduleEntity, event, EventAction.Add
                            )
                            appUiState.appBackStack.onBack()
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
                            CustomFilterChip(
                                title = type
                                    ?: stringResource(R.string.not_specified),
                                imageVector = null,
                                selected = type == typeEvent,
                                onSelect = {
                                    typeEvent = type
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }
                }
            )

            scheduleEntity.recurrence?.let {
                DateSelector(
                    currentDateTime = currentDateTime,
                    dateEvent = dateEvent,
                    onSelectDateEvent = { value ->
                        dateEvent = value
                    },
                    focusManager = focusManager
                )
                TimeSelector(
                    focusManager = focusManager,
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
                    maxInterval = scheduleEntity.recurrence.interval,
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
                focusManager = focusManager,
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
                    focusManager.clearFocus()
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
                title = stringResource(R.string.lecturers),
                elements = lecturersList,
                onAddElement = {
                    lecturersList = (lecturersList + DefaultEventParams.defaultLecturer)
                    focusManager.clearFocus()
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
                    focusManager.clearFocus()
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