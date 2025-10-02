package com.egormelnikoff.schedulerutmiit.ui.dialogs

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.GridGroup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddScheduleDialog(
    onBack: () -> Unit,
    onAddCustomSchedule: (Triple<String, LocalDate, LocalDate>) -> Unit,
    onShowErrorMessage: (String) -> Unit,

    focusManager: FocusManager,
    externalPadding: PaddingValues
) {
    val context = LocalContext.current

    var nameSchedule by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var showDialogStartDate by remember { mutableStateOf(false) }
    var showDialogEndDate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = LocalContext.current.getString(R.string.adding_a_schedule),
                navAction = { onBack() }
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
                    top = innerPadding.calculateTopPadding(),
                    bottom = externalPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GridGroup(
                items = listOf(
                    listOf {
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = nameSchedule,
                            onValueChanged = { newValue ->
                                nameSchedule = newValue
                            },
                            placeholderText = LocalContext.current.getString(R.string.schedule_name),
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                imeAction = ImeAction.Done
                            )
                        )
                    },
                    listOf(
                        {
                            ChooseDateTimeButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    focusManager.clearFocus()
                                    showDialogStartDate = true
                                },
                                title = startDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                    ?: LocalContext.current.getString(R.string.start_date)
                            )
                        }, {
                            ChooseDateTimeButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    focusManager.clearFocus()
                                    showDialogEndDate = true
                                },
                                title = endDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                    ?: LocalContext.current.getString(R.string.end_date),
                                enabled = startDate != null
                            )
                        }
                    )
                )
            )
            CustomButton(
                modifier = Modifier.padding(top = 8.dp),
                buttonTitle = LocalContext.current.getString(R.string.create),
                onClick = {
                    val errorMessages =
                        checkScheduleParams(context, nameSchedule, startDate, endDate)
                    if (errorMessages.isEmpty()) {
                        onAddCustomSchedule(Triple(nameSchedule.trim(), startDate!!, endDate!!))
                    } else {
                        onShowErrorMessage(errorMessages)
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

fun checkScheduleParams(
    context: Context,
    name: String,
    startDate: LocalDate?,
    endDate: LocalDate?,
): String {
    val errorString = StringBuilder().apply {
        if (name.isEmpty()) {
            append("${context.getString(R.string.no_name_specified)}\n")
        }
        if (startDate == null) {
            append("${context.getString(R.string.no_start_time_specified)}\n")
        }
        if (endDate == null) {
            append("${context.getString(R.string.no_end_time_specified)}\n")
        }
    }.trimEnd()
    return errorString.toString()
}