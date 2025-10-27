package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class DateRangeSelectableDates(
    private val startMillis: Long,
    private val endMillis: Long
) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis in startMillis..endMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        val yearStart =
            LocalDate.of(year, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val yearEnd =
            LocalDate.of(year, 12, 31).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        return startMillis <= yearEnd && endMillis >= yearStart
    }
}

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
    val minMillis = minDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val maxMillis = maxDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            ?.plusDays(1)
            ?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli(),
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = DateRangeSelectableDates(minMillis, maxMillis)
    )
    CustomModalBottomSheet(
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
        CustomButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            enabled = datePickerState.selectedDateMillis != null,
            buttonTitle = LocalContext.current.getString(R.string.confirm),
            onClick = {
                val instant = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!)
                onDateSelect(instant.atZone(ZoneId.systemDefault()).toLocalDate())
                onShowDialog(false)
            }
        )
    }
}
