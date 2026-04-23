package com.egormelnikoff.schedulerutmiit.ui.dialogs

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.app.validator.isValidSchedule
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.ChooseDateTimeButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.GridGroup
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleViewModel
import java.time.LocalDate

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AddScheduleDialog(
    appUiState: AppUiState,
    scheduleViewModel: ScheduleViewModel
) {
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
                            ChooseDateTimeButton(
                                modifier = Modifier.fillMaxWidth(),
                                title = startDate?.format(dayMonthYearFormatter)
                                    ?: stringResource(R.string.start_date)
                            ) {
                                focusManager.clearFocus()
                                showDialogStartDate = true
                            }
                        }, {
                            ChooseDateTimeButton(
                                modifier = Modifier.fillMaxWidth(),
                                title = endDate?.format(dayMonthYearFormatter)
                                    ?: stringResource(R.string.end_date),
                                enabled = startDate != null
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
    }
}