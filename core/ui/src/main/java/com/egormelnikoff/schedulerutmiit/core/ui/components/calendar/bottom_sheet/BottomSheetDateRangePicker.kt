package com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.bottom_sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
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

    AppBottomSheet(
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
                    modifier = Modifier.padding(bottom = AppTheme.paddings.padding12)
                ) {
                    AppButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.paddings.padding16),
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
                containerColor = AppTheme.colors.background,
                titleContentColor = AppTheme.colors.onBackground,
                dividerColor = AppTheme.colors.outline,
                dateTextFieldColors = TextFieldDefaults.colors(
                    focusedContainerColor = AppTheme.colors.background,
                    unfocusedContainerColor = AppTheme.colors.background,

                    focusedTextColor = AppTheme.colors.onBackground,
                    unfocusedTextColor = AppTheme.colors.onSecondaryContainer,

                    focusedIndicatorColor = AppTheme.colors.primary,
                    unfocusedIndicatorColor = AppTheme.colors.outline,

                    focusedLabelColor = AppTheme.colors.primary,
                    unfocusedLabelColor = AppTheme.colors.outline,

                    errorLabelColor = AppTheme.colors.error,
                    errorContainerColor = AppTheme.colors.background,
                    errorIndicatorColor = AppTheme.colors.error
                )
            )
        )
    }
}
