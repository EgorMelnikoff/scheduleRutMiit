package com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.add_task

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
import androidx.compose.runtime.getValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.time_picker.AppTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ColorSelectorWidget
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.text_field.AppTextField
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.LeadingIcon
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.add_task.view_model.AddTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onBack: () -> Unit
) {
    val addTaskViewModel = hiltViewModel<AddTaskViewModel>()
    val addTaskForm = addTaskViewModel.addTaskForm.collectAsStateWithLifecycle().value

    var showDialogDate by remember { mutableStateOf(false) }
    var showDialogTime by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AppTopBar(
                titleText = stringResource(R.string.create_task),
                navAction = onBack
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
                AppTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = addTaskForm.text,
                    placeholderText = stringResource(R.string.class_name),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false, imeAction = ImeAction.Done
                    )
                ) { newValue ->
                    addTaskViewModel.onTextChanged(newValue)
                }

                ColumnGroup(
                    items = listOf(
                        {
                            AppCell(
                                defaultMinHeight = 32.dp,
                                showClickLabel = false,
                                title = when {
                                    addTaskForm.startDate != null && addTaskForm.startDate == addTaskForm.endDate -> "${
                                        addTaskForm.startDate.format(
                                            DateTimeFormatters.dayMonthYearFormatter
                                        )
                                    }"

                                    addTaskForm.startDate != null && addTaskForm.endDate != null -> "${
                                        addTaskForm.startDate.format(
                                            DateTimeFormatters.dayMonthYearFormatter
                                        )
                                    }" +
                                            " - ${addTaskForm.endDate.format(DateTimeFormatters.dayMonthYearFormatter)}"

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
                            AppCell(
                                defaultMinHeight = 32.dp,
                                showClickLabel = false,
                                title = addTaskForm.time?.format(DateTimeFormatters.hourMinuteFormatter)
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
                        ColorSelectorWidget(
                            currentSelected = addTaskForm.tag,
                            onColorSelect = { newTag ->
                                addTaskViewModel.onTagChanged(newTag)
                            }
                        )
                    }
                )
            }
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                buttonTitle = stringResource(R.string.create),
                enabled = addTaskForm.isButtonEnabled,
                onClick = {
                    addTaskViewModel.addTask()
                    onBack()
                }
            )
        }

        if (showDialogDate) {
            // TODO даты
//            BottomSheetDateRangePicker(
//                selectableStartDate = calendarData.startDate,
//                selectableEndDate = calendarData.endDate,
//
//                selectedStartDate = addTaskForm.startDate,
//                selectedEndDate = addTaskForm.endDate,
//
//                onShowDialog = {
//                    showDialogDate = it
//                },
//                onDateSelect = { start, end ->
//                    addTaskViewModel.onDatesChanged(start, end)
//                }
//            )
        }
        if (showDialogTime) {
            AppTimePicker(
                selectedTime = addTaskForm.time,
                onTimeSelect = { newValue ->
                    addTaskViewModel.onTimeChanged(newValue)
                },
                onShowDialog = { newValue -> showDialogTime = newValue },
            )
        }
    }
}
