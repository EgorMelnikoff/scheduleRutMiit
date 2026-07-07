package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButtonRow
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet.BottomSheetDateRangePicker
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.view_model.AddScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AddScheduleDialog(
    onBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    val addScheduleViewModel = hiltViewModel<AddScheduleViewModel>()
    val form = addScheduleViewModel.form.collectAsStateWithLifecycle().value

    var showBackDialog by remember { mutableStateOf(false) }
    var showDialogDate by remember { mutableStateOf(false) }

    BackHandler {
        showBackDialog = true
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = stringResource(R.string.create_schedule),
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ColumnGroup(
                    modifier = Modifier,
                    items = listOf(
                        {
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = form.name,
                                placeholderText = stringResource(R.string.schedule_name),
                                keyboardOptions = KeyboardOptions(
                                    autoCorrectEnabled = false,
                                    imeAction = ImeAction.Done
                                )
                            ) { newValue ->
                                addScheduleViewModel.updateName(newValue)
                            }
                        }, {
                            ClickableItem(
                                defaultMinHeight = 32.dp,
                                showClickLabel = false,
                                title = when {
                                    form.startDate != null && form.startDate == form.endDate ->
                                        "${form.startDate.format(dayMonthYearFormatter)}"

                                    form.startDate != null && form.endDate != null ->
                                        "${form.startDate.format(dayMonthYearFormatter)}" +
                                                " - ${form.endDate.format(dayMonthYearFormatter)}"

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
                        }
                    )
                )
                CustomButtonRow(
                    selectedElement = form.timetableType.isPeriodic(),
                    elements = listOf(true, false),
                    colors = SegmentedButtonDefaults.colors().copy(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeBorderColor = Color.Transparent,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        inactiveBorderColor = Color.Transparent,
                        inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    onClick = { value ->
                        addScheduleViewModel.updateTimetableType(
                            if (value) TimetableType.PERIODIC
                            else TimetableType.NON_PERIODIC
                        )
                    }
                ) { value ->
                    Text(
                        text = if (value.second) TimetableType.PERIODIC.typeName
                        else TimetableType.NON_PERIODIC.typeName,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            CustomButton(
                modifier = Modifier.fillMaxWidth(),
                buttonTitle = stringResource(R.string.create),
                enabled = form.isButtonEnabled,
                onClick = {
                    addScheduleViewModel.addCustomNamedSchedule()
                }
            )
        }

        if (showDialogDate) {
            BottomSheetDateRangePicker(
                selectedStartDate = form.startDate,
                selectedEndDate = form.endDate,
                onDateSelect = { startDate, endDate ->
                    addScheduleViewModel.updateDates(startDate, endDate)
                },
                onShowDialog = {
                    showDialogDate = it
                }
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