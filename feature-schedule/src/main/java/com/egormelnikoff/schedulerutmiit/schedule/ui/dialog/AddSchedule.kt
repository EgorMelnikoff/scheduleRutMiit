package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.GridGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.schedule.data.validator.isValidSchedule
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.AppUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import java.time.LocalDate

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AddScheduleDialog(
    appUiState: AppUiState,
    scheduleViewModel: ScheduleViewModel
) {
    var showBackDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    var nameSchedule by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var showDialogStartDate by remember { mutableStateOf(false) }
    var showDialogEndDate by remember { mutableStateOf(false) }


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
                navAction = { appUiState.appBackStack.onBack() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding() + 8.dp
                )
        ) {
            GridGroup(
                modifier = Modifier.align(Alignment.TopCenter),
                items = listOf(
                    listOf {
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
                    },
                    listOf(
                        {
                            ClickableItem(
                                defaultMinHeight = 32.dp,
                                showClickLabel = false,
                                title = startDate?.format(dayMonthYearFormatter)
                                    ?: stringResource(R.string.start_date),
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
                                showDialogStartDate = true
                            }
                        }, {
                            ClickableItem(
                                defaultMinHeight = 32.dp,
                                showClickLabel = false,
                                title = endDate?.format(dayMonthYearFormatter)
                                    ?: stringResource(R.string.end_date),
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
                                showDialogEndDate = true
                            }
                        }
                    )
                )
            )
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                buttonTitle = stringResource(R.string.create),
                enabled = buttonEnabled,
                onClick = {
                    startDate?.let { startDate ->
                        endDate?.let { endDate ->
                            scheduleViewModel.addCustomNamedSchedule(
                                nameSchedule.trim(),
                                startDate,
                                endDate
                            )
                            appUiState.appBackStack.navigateToStartRage()
                            appUiState.appBackStack.onBack()
                        }
                    }
                }
            )
        }

        if (showDialogStartDate) {
            BottomSheetDatePicker(
                selectedDate = startDate,
                onDateSelect = { newValue ->
                    startDate = newValue
                },
                onShowDialog = { newValue ->
                    showDialogStartDate = newValue
                },
                endDate = endDate
            )
        }
        if (showDialogEndDate) {
            BottomSheetDatePicker(
                selectedDate = endDate,
                onDateSelect = { newValue ->
                    endDate = newValue
                },
                onShowDialog = { newValue ->
                    showDialogEndDate = newValue
                },
                startDate = startDate
            )
        }

        if (showBackDialog) {
            CustomAlertDialog(
                dialogTitle = stringResource(R.string.exit),
                dialogText = stringResource(R.string.exit_message),
                onDismissRequest = {
                    showBackDialog = false
                },
                onConfirmation = {
                    appUiState.appBackStack.onBack()
                }
            )
        }
    }
}