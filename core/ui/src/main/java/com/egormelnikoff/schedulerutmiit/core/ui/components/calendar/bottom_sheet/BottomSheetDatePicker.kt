package com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.bottom_sheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
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
fun BottomSheetDatePicker(
    selectedDate: LocalDate?,
    onDateSelect: (LocalDate) -> Unit,
    onShowDialog: (Boolean) -> Unit,
    startDate: LocalDate? = null,
    endDate: LocalDate? = null
) {
    val today = LocalDate.now()
    val minDate = startDate ?: today
    val maxDate = endDate ?: today.plusYears(1)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            ?.plusDays(1)
            ?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli(),
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = DateRangeSelectableDates(
            minDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            maxDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
    )
    AppBottomSheet(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        sheetState = sheetState,
        onDismiss = {
            onShowDialog(false)
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
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
        AppButton(
            modifier = Modifier.fillMaxWidth().padding(horizontal = AppTheme.paddings.padding16),
            enabled = datePickerState.selectedDateMillis != null,
            buttonTitle = stringResource(R.string.confirm),
            onClick = {
                datePickerState.selectedDateMillis?.let {
                    val instant = Instant.ofEpochMilli(it)
                    onDateSelect(instant.atZone(ZoneId.systemDefault()).toLocalDate())
                    onShowDialog(false)
                }
            }
        )
    }
}
