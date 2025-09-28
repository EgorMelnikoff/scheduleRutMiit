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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.GridGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.SimpleTopBar
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun AddScheduleDialog(
    scheduleViewModel: ScheduleViewModel,
    snackbarHostState: SnackbarHostState,
    focusManager: FocusManager,
    scope: CoroutineScope,
    onBack: () -> Unit,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    var nameSchedule by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var dialogStartDate by remember { mutableStateOf(false) }
    var dialogEndDate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = LocalContext.current.getString(R.string.adding_a_schedule),
                navAction = {
                    onBack()
                },
                navImageVector = ImageVector.vectorResource(R.drawable.back)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),

            ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                        dialogStartDate = true
                                        focusManager.clearFocus()
                                    },
                                    title = startDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                        ?: LocalContext.current.getString(R.string.start_date)
                                )
                            }, {
                                ChooseDateTimeButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        dialogEndDate = true
                                        focusManager.clearFocus()
                                    },
                                    title = endDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                        ?: LocalContext.current.getString(R.string.end_date),
                                    enabled = startDate != null
                                )
                            }
                        )
                    )
                )
            }
            CustomButton(
                modifier = Modifier.padding(top = 8.dp),
                title = LocalContext.current.getString(R.string.create),
                onClick = {
                    val errorMessages =
                        checkScheduleParams(context, nameSchedule, startDate, endDate)
                    if (errorMessages.isEmpty()) {
                        scheduleViewModel.addCustomSchedule(
                            name = nameSchedule.trim(),
                            startDate = startDate!!,
                            endDate = endDate!!,
                        )
                        onBack()
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessages,
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
            )
        }
        if (dialogStartDate) {
            BottomSheetDatePicker(
                selectedDate = startDate,
                onDateSelect = { newValue -> startDate = newValue },
                onShowDialog = { newValue -> dialogStartDate = newValue },
                endDate = endDate
            )
        }
        if (dialogEndDate) {
            BottomSheetDatePicker(
                selectedDate = endDate,
                onDateSelect = { newValue -> endDate = newValue },
                onShowDialog = { newValue -> dialogEndDate = newValue },
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