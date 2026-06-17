package com.egormelnikoff.schedulerutmiit.tasks.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarData
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    editableTask: Task,
    updateText: (String) -> Unit,
    updateTag: (Int) -> Unit,
    updateTime: (LocalTime) -> Unit,
    calendarData: CalendarData,
    onBack: () -> Unit
) {
    var task by remember { mutableStateOf(editableTask) }

    var showDialogDate by remember { mutableStateOf(false) }
    var showDialogTime by remember { mutableStateOf(false) }

    var taskText by remember { mutableStateOf(task.text) }
    var tag by remember { mutableIntStateOf(task.tag) }
    var date by remember { mutableStateOf(task.date) }
    var time by remember { mutableStateOf(task.time) }

    val buttonEnabled by remember {
        derivedStateOf {
            (taskText != task.text || tag != task.tag || time != task.time) && taskText.isNotBlank()
        }
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = stringResource(R.string.task), navAction = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 8.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = taskText,
                    placeholderText = stringResource(R.string.class_name),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false, imeAction = ImeAction.Done
                    )
                ) { newValue ->
                    taskText = newValue
                    //updateText(newValue)
                }

                ColumnGroup(
                    items = listOf {
                        ClickableItem(
                            defaultMinHeight = 32.dp,
                            showClickLabel = false,
                            title = time.format(DateTimeFormatters.hourMinuteFormatter),
                            titleTypography = MaterialTheme.typography.titleSmall,
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.time),
                                    iconSize = 20.dp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        ) {
                            focusManager.clearFocus()
                            showDialogTime = true
                        }
                    }
                )

                ColumnGroup(
                    title = stringResource(R.string.tag),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false,
                    items = listOf {
                        ColorSelector(
                            currentSelected = tag, onColorSelect = { newTag ->
                                tag = newTag
                                //updateTag(newTag)
                            }
                        )
                    }
                )
            }

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth(),
                buttonTitle = stringResource(R.string.save),
                enabled = buttonEnabled,
                onClick = {
                    if (taskText != task.text) updateText(taskText)
                    if (tag != task.tag) updateTag(tag)
                    if (time != task.time) updateTime(time)
                    task = editableTask.copy(
                        text = taskText,
                        tag = tag
                    )
                }
            )
        }
    }

    if (showDialogDate) {
        BottomSheetDatePicker(
            selectedDate = date,
            onDateSelect = { newValue -> date = newValue },
            onShowDialog = { newValue -> showDialogDate = newValue },
            startDate = calendarData.startDate,
            endDate = calendarData.endDate,
        )
    }

    if (showDialogTime) {
        BottomSheetTimePicker(
            selectedTime = time,
            onTimeSelect = { newValue ->
                time = newValue

            },
            onShowDialog = { newValue -> showDialogTime = newValue },
        )
    }
}
