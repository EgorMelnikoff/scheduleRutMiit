package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.DefaultEventParams
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.RecurrenceEvent
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.common.extension.toUtcDateTime
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetDatePicker
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
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.schedule.data.validator.isValidEvent
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventDialog(
    addEditEventDialog: Route.Dialog.AddEditEventDialog,
    addEvent: (Long, Event) -> Unit,
    editEvent: (Long, Event) -> Unit,
    currentDate: LocalDate,
    scope: CoroutineScope,
    onBack: () -> Unit
) {
    var showBackDialog by remember { mutableStateOf(false) }

    BackHandler {
        showBackDialog = true
    }


    val focusManager = LocalFocusManager.current

    var currentInterval by remember {
        mutableIntStateOf(
            addEditEventDialog.updatableEvent?.recurrenceRule?.interval ?: -1
        )
    }
    var currentPeriod by remember {
        mutableIntStateOf(
            addEditEventDialog.updatableEvent?.periodNumber ?: 1
        )
    }
    var showDialogDate by remember { mutableStateOf(false) }
    var showDialogStart by remember { mutableStateOf(false) }
    var showDialogEnd by remember { mutableStateOf(false) }

    var nameEvent by remember { mutableStateOf(addEditEventDialog.updatableEvent?.name ?: "") }
    var typeEvent by remember {
        mutableStateOf(
            addEditEventDialog.updatableEvent?.typeName ?: DefaultEventParams.types.first()
        )
    }
    var dateEvent by remember { mutableStateOf(addEditEventDialog.updatableEvent?.startDatetime?.toLocalDate()) }
    var startTime by remember { mutableStateOf(addEditEventDialog.updatableEvent?.startDatetime?.toLocalTimeWithTimeZone()) }
    var endTime by remember { mutableStateOf(addEditEventDialog.updatableEvent?.endDatetime?.toLocalTimeWithTimeZone()) }

    var roomsList by remember {
        mutableStateOf(
            addEditEventDialog.updatableEvent?.rooms ?: listOf()
        )
    }
    var lecturersList by remember {
        mutableStateOf(
            addEditEventDialog.updatableEvent?.lecturers ?: listOf()
        )
    }
    var groupsList by remember {
        mutableStateOf(
            addEditEventDialog.updatableEvent?.groups ?: listOf()
        )
    }

    val buttonEnabled by remember {
        derivedStateOf {
            isValidEvent(
                name = nameEvent,
                date = dateEvent,
                startTime = startTime,
                endTime = endTime,
                roomsList = roomsList,
                lecturersList = lecturersList,
                groupsList = groupsList
            )
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                shadowElevation = 4.dp,
                titleText = addEditEventDialog.updatableEvent?.let {
                    stringResource(R.string.editing)
                } ?: stringResource(R.string.create_class),
                navAction = onBack
            )
        }
    ) { innerPadding ->
        val pagerState = rememberPagerState(
            initialPage = 0
        ) { 3 }

        PagerScreenContainer(
            pagerState = pagerState,
            scope = scope,
            isNextEnabled = { page ->
                when (page) {
                    0 -> nameEvent.isNotBlank()
                    1 -> dateEvent != null && startTime != null && endTime != null && (addEditEventDialog.recurrence != null && currentInterval != -1 || addEditEventDialog.recurrence == null)
                    2 -> buttonEnabled
                    else -> true
                }
            },
            onFinish = {
                dateEvent?.let {
                    val startDateTime = startTime?.toUtcDateTime(it)
                    val endDateTime = endTime?.toUtcDateTime(it)
                    if (startDateTime != null && endDateTime != null) {
                        val event = Event(
                            id = addEditEventDialog.updatableEvent?.id ?: 0,
                            scheduleId = addEditEventDialog.scheduleId,
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
                            recurrenceRule = addEditEventDialog.recurrence?.let {
                                RecurrenceEvent(
                                    frequency = "WEEKLY",
                                    interval = currentInterval
                                )
                            },
                            periodNumber = if (currentInterval > 1) currentPeriod else 1
                        )
                        if (addEditEventDialog.updatableEvent != null) {
                            editEvent(
                                addEditEventDialog.namedScheduleId,
                                event
                            )
                        } else {
                            addEvent(
                                addEditEventDialog.namedScheduleId, event
                            )
                        }
                    }
                }
                onBack()
            },
            paddingValues = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding()
            ),
            finishTitle = if (addEditEventDialog.updatableEvent != null) {
                stringResource(R.string.edit)
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
                    }
                }

                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                    ) {
                        if (addEditEventDialog.recurrence != null) {
                            DaySelector(
                                currentDate = currentDate,
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
                                maxInterval = requireNotNull(addEditEventDialog.recurrence).interval,
                                currentInterval = currentInterval,
                                currentPeriod = currentPeriod,
                                onSelectInterval = { value ->
                                    currentInterval = value
                                },
                                onSelectPeriod = { value ->
                                    currentPeriod = value
                                },
                            )
                        } else {
                            DateTimeSelector(
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
                                        roomsList.toMutableList()
                                            .apply { this[index] = updatedRoom }
                                },
                                onRemove = {
                                    roomsList =
                                        roomsList.toMutableList().apply { removeAt(index) }
                                }
                            )
                        }
                        ListParam(
                            title = stringResource(R.string.lecturers),
                            elements = lecturersList,
                            onAddElement = {
                                lecturersList =
                                    (lecturersList + DefaultEventParams.defaultLecturer)
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
                                        groupsList.toMutableList()
                                            .apply { this[index] = updatedGroup }
                                },
                                onRemove = {
                                    groupsList =
                                        groupsList.toMutableList().apply { removeAt(index) }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showDialogDate) {
            BottomSheetDatePicker(
                selectedDate = dateEvent,
                onDateSelect = { newValue -> dateEvent = newValue },
                onShowDialog = { newValue -> showDialogDate = newValue },
                startDate = addEditEventDialog.scheduleStartDate,
                endDate = addEditEventDialog.scheduleEndDate
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

