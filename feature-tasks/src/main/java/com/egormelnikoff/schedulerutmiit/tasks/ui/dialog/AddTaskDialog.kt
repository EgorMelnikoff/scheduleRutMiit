package com.egormelnikoff.schedulerutmiit.tasks.ui.dialog

import androidx.activity.compose.BackHandler
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
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet.BottomSheetDateRangePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarData
import com.egormelnikoff.schedulerutmiit.tasks.data.repos.CreateTask
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    addTask: (CreateTask) -> Unit,
    calendarData: CalendarData,
    onBack: () -> Unit
) {
    var showBackDialog by remember { mutableStateOf(false) }
    var showDialogDate by remember { mutableStateOf(false) }
    var showDialogTime by remember { mutableStateOf(false) }

    BackHandler {
        showBackDialog = true
    }

    var taskText by remember { mutableStateOf("") }
    var tag by remember { mutableIntStateOf(0) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf<LocalTime?>(null) }

    val buttonEnabled by remember {
        derivedStateOf {
            taskText.isNotBlank() && startDate != null && endDate != null && time != null
        }
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = "Создать задание", navAction = onBack
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
                }

                ColumnGroup(
                    items = listOf(
                        {
                            ClickableItem(
                                defaultMinHeight = 32.dp,
                                showClickLabel = false,
                                title = when {
                                    startDate != null && startDate == endDate -> "${
                                        startDate?.format(
                                            DateTimeFormatters.dayMonthYearFormatter
                                        )
                                    }"

                                    startDate != null && endDate != null -> "${
                                        startDate?.format(
                                            DateTimeFormatters.dayMonthYearFormatter
                                        )
                                    }" +
                                            " - ${endDate?.format(DateTimeFormatters.dayMonthYearFormatter)}"

                                    else -> stringResource(R.string.dates)
                                },
                                titleTypography = MaterialTheme.typography.titleSmall,
                                leadingIcon = {
                                    LeadingIcon(
                                        imageVector = ImageVector.vectorResource(R.drawable.calendar),
                                        iconSize = 20.dp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            ) {
                                focusManager.clearFocus()
                                showDialogDate = true
                            }
                        }, {
                            ClickableItem(
                                defaultMinHeight = 32.dp,
                                showClickLabel = false,
                                title = time?.format(DateTimeFormatters.hourMinuteFormatter)
                                    ?: stringResource(R.string.time),
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
                )

                ColumnGroup(
                    title = stringResource(R.string.tag),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false,
                    items = listOf {
                        ColorSelector(
                            currentSelected = tag, onColorSelect = { newTag ->
                                tag = newTag
                            }
                        )
                    }
                )
            }
            CustomButton(
                modifier = Modifier.fillMaxWidth(),
                buttonTitle = stringResource(R.string.create),
                enabled = buttonEnabled,
                onClick = {
                    addTask(
                        CreateTask(
                            taskText, tag, time!!, startDate!!, endDate!!
                        )
                    )
                }
            )
        }

        if (showDialogDate) {
            BottomSheetDateRangePicker(
                selectableStartDate = calendarData.startDate,
                selectableEndDate = calendarData.endDate,

                selectedStartDate = startDate,
                selectedEndDate = endDate,

                onShowDialog = {
                    showDialogDate = it
                },
                onDateSelect = { start, end ->
                    startDate = start
                    endDate = end
                }
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
}
