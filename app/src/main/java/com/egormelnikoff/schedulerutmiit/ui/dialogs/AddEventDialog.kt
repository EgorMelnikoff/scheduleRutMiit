package com.egormelnikoff.schedulerutmiit.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.Group
import com.egormelnikoff.schedulerutmiit.data.entity.Lecturer
import com.egormelnikoff.schedulerutmiit.data.entity.Room
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetTimePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomChip
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.GridGroup
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DefaultEventParams {
    val types = arrayOf(
        null,
        "Лекция",
        "Лабораторная работа",
        "Практическое занятие",
        "Консультация",
        "Зачёт",
        "Экзамен",
        "Комиссия",
        "Другое"
    )
    val defaultRoom = Room(
        id = null,
        url = null,
        name = "",
        hint = ""
    )
    val defaultLecturer = Lecturer(
        id = null,
        shortFio = "",
        fullFio = "",
        url = null,
        description = null,
        hint = null
    )
    val defaultGroup = Group(
        id = null,
        url = null,
        name = ""
    )
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun AddEventDialog(
    onBack: () -> Unit,
    onAddCustomEvent: (Event) -> Unit,
    onShowErrorMessage: (String) -> Unit,
    scheduleEntity: ScheduleEntity,
    focusManager: FocusManager,
    externalPadding: PaddingValues,
) {
    val context = LocalContext.current
    var showDialogDate by remember { mutableStateOf(false) }
    var showDialogStart by remember { mutableStateOf(false) }
    var showDialogEnd by remember { mutableStateOf(false) }

    var nameEvent by remember { mutableStateOf("") }
    var typeEvent by remember { mutableStateOf(DefaultEventParams.types.first()) }
    var dateEvent by remember { mutableStateOf<LocalDate?>(null) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }

    var roomsList by remember { mutableStateOf(listOf<Room>()) }
    var lecturersList by remember { mutableStateOf(listOf<Lecturer>()) }
    var groupsList by remember { mutableStateOf(listOf<Group>()) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = LocalContext.current.getString(R.string.adding_a_class),
                navAction = {
                    onBack()
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
                    top = innerPadding.calculateTopPadding(),
                    bottom = externalPadding.calculateBottomPadding()
                )
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = nameEvent,
                    onValueChanged = { newValue ->
                        nameEvent = newValue
                    },
                    placeholderText = LocalContext.current.getString(R.string.class_name),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Done
                    )
                )

                ColumnGroup(
                    title = LocalContext.current.getString(R.string.class_type),
                    backgroundColor = Color.Unspecified,
                    items = listOf{
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
                                        ?: LocalContext.current.getString(R.string.not_specified),
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
                GridGroup(
                    title = LocalContext.current.getString(R.string.date_and_time),
                    items = listOf(
                        listOf {
                            ChooseDateTimeButton(
                                modifier = Modifier.fillMaxWidth(),
                                title = dateEvent?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                    ?: LocalContext.current.getString(R.string.date),
                                onClick = {
                                    showDialogDate = true
                                    focusManager.clearFocus()
                                }
                            )
                        },
                        listOf(
                            {
                                ChooseDateTimeButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    title = startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                                        ?: LocalContext.current.getString(R.string.start_time),
                                    imageVector = ImageVector.vectorResource(R.drawable.time),
                                    onClick = {
                                        showDialogStart = true
                                        focusManager.clearFocus()
                                    }
                                )
                            }, {
                                ChooseDateTimeButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    title = endTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                                        ?: LocalContext.current.getString(R.string.end_time),
                                    imageVector = ImageVector.vectorResource(R.drawable.time),
                                    onClick = {
                                        showDialogEnd = true
                                        focusManager.clearFocus()
                                    },
                                    enabled = startTime != null
                                )
                            }

                        )
                    )
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                )
                ListParam(
                    title = LocalContext.current.getString(R.string.Room),
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
                    title = LocalContext.current.getString(R.string.Lecturers),
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
                            lecturersList = lecturersList.toMutableList().apply { removeAt(index) }
                        }
                    )
                }
                ListParam(
                    title = LocalContext.current.getString(R.string.Groups),
                    elements = groupsList,
                    onAddElement = {
                        groupsList = (groupsList + DefaultEventParams.defaultGroup)
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
            CustomButton(
                modifier = Modifier.padding(top = 8.dp),
                buttonTitle = LocalContext.current.getString(R.string.add),
                onClick = {
                    val errorMessages = checkEventParams(
                        context = context,
                        name = nameEvent,
                        date = dateEvent,
                        startTime = startTime,
                        endTime = endTime,
                        roomsList = roomsList,
                        lecturersList = lecturersList,
                        groupsList = groupsList
                    )
                    if (errorMessages.isEmpty()) {
                        val event = Event(
                            scheduleId = scheduleEntity.id,
                            name = nameEvent.trim(),
                            typeName = typeEvent,

                            startDatetime = LocalDateTime.of(dateEvent, startTime)
                                .atZone(ZoneId.systemDefault())
                                .withZoneSameInstant(ZoneOffset.UTC)
                                .toLocalDateTime(),
                            endDatetime = LocalDateTime.of(dateEvent, endTime)
                                .atZone(ZoneId.systemDefault())
                                .withZoneSameInstant(ZoneOffset.UTC)
                                .toLocalDateTime(),
                            lecturers = lecturersList,
                            rooms = roomsList,
                            isCustomEvent = true,
                            groups = null,
                            timeSlotName = null,
                            recurrenceRule = null,
                            periodNumber = null
                        )
                        onAddCustomEvent(event)
                        onBack()
                    } else {
                        onShowErrorMessage(errorMessages)
                    }
                }
            )
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
fun LecturerInput(
    lecturer: Lecturer,
    onValueChanged: (Lecturer) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextField(
            modifier = Modifier.weight(1f),
            value = lecturer.shortFio ?: "",
            onValueChanged = { newValue ->
                onValueChanged(lecturer.copy(shortFio = newValue, fullFio = newValue))
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            placeholderText = LocalContext.current.getString(R.string.full_name_of_lecturer),
        )
        RemoveButton { onRemove() }
    }
}


@Composable
fun RoomInput(
    room: Room,
    onValueChanged: (Room) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextField(
            modifier = Modifier.weight(1f),
            value = room.name ?: "",
            onValueChanged = { newValue ->
                onValueChanged(room.copy(name = newValue, hint = newValue))
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            placeholderText = LocalContext.current.getString(R.string.room_number),
        )
        RemoveButton { onRemove() }
    }
}

@Composable
fun GroupInput(
    group: Group,
    onValueChanged: (Group) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextField(
            modifier = Modifier.weight(1f),
            value = group.name ?: "",
            onValueChanged = { newValue ->
                onValueChanged(group.copy(name = newValue))
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            placeholderText = LocalContext.current.getString(R.string.group_number),
        )
        RemoveButton { onRemove() }
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

        if (startTime != null && endTime != null && startTime >= endTime) {
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