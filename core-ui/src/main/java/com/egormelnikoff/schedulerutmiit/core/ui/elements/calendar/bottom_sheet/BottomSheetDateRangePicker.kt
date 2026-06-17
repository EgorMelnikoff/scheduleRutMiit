package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomModalBottomSheet
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDateRangePicker(
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    onDateSelect: (LocalDate, LocalDate) -> Unit,
    onShowDialog: (Boolean) -> Unit,
    selectableStartDate: LocalDate? = null,
    selectableEndDate: LocalDate? = null
) {
    val today = LocalDate.now()
    val minDate = selectableStartDate ?: today
    val maxDate = selectableEndDate ?: today.plusYears(1)

    val sheetState = rememberModalBottomSheetState()
    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDate = selectedStartDate,
        initialSelectedEndDate = selectedEndDate,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = DateRangeSelectableDates(
            minDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            maxDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
    )

    CustomModalBottomSheet(
        horizontalAlignment = Alignment.CenterHorizontally,
        sheetState = sheetState,
        onDismiss = {
            onShowDialog(false)
        }
    ) {
        DateRangePicker(
            modifier = Modifier.weight(1f),
            state = datePickerState,
            showModeToggle = false,
            title = null,
            headline = {
                Box(
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        enabled = datePickerState.selectedStartDateMillis != null,
                        buttonTitle = stringResource(R.string.confirm),
                        onClick = {
                            datePickerState.selectedStartDateMillis?.let { s ->
                                val startDate = Instant
                                    .ofEpochMilli(s)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                                val endDate = datePickerState.selectedEndDateMillis?.let {
                                    Instant
                                        .ofEpochMilli(it)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                } ?: startDate

                                onDateSelect(startDate, endDate)
                                onShowDialog(false)

                            }
                        }
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                dividerColor = MaterialTheme.colorScheme.outline,
                dateTextFieldColors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,

                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,

                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,

                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.outline,

                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorContainerColor = MaterialTheme.colorScheme.background,
                    errorIndicatorColor = MaterialTheme.colorScheme.error
                )
            )
        )
    }
}
