package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog

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
import androidx.compose.runtime.derivedStateOf
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
import com.egormelnikoff.schedulerutmiit.schedule.data.validator.isValidSchedule
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AddScheduleDialog(
    addSchedule: (String, LocalDate, LocalDate, TimetableType) -> Unit,
    onBack: () -> Unit
) {
    var showBackDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    var nameSchedule by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var type by remember { mutableStateOf(TimetableType.NON_PERIODIC) }

    var showDialogDate by remember { mutableStateOf(false) }

    val buttonEnabled by remember {
        derivedStateOf {
            isValidSchedule(
                name = nameSchedule,
                start = startDate,
                end = endDate
            )
        }
    }

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
                                value = nameSchedule,
                                placeholderText = stringResource(R.string.schedule_name),
                                keyboardOptions = KeyboardOptions(
                                    autoCorrectEnabled = false,
                                    imeAction = ImeAction.Done
                                )
                            ) { newValue ->
                                nameSchedule = newValue
                            }
                        }, {
                            ClickableItem(
                                defaultMinHeight = 32.dp,
                                showClickLabel = false,
                                title = when {
                                    startDate != null && startDate == endDate ->
                                        "${startDate?.format(dayMonthYearFormatter)}"

                                    startDate != null && endDate != null ->
                                        "${startDate?.format(dayMonthYearFormatter)}" +
                                                " - ${endDate?.format(dayMonthYearFormatter)}"

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
                    selectedElement = type.isPeriodic(),
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
                        type = if (value) TimetableType.PERIODIC
                        else TimetableType.NON_PERIODIC
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
                enabled = buttonEnabled,
                onClick = {
                    startDate?.let { startDate ->
                        endDate?.let { endDate ->
                            addSchedule(nameSchedule.trim(), startDate, endDate, type)
                        }
                    }
                }
            )
        }

        if (showDialogDate) {
            BottomSheetDateRangePicker(
                selectedStartDate = startDate,
                selectedEndDate = endDate,
                onDateSelect = { start, end ->
                    startDate = start
                    endDate = end
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