package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetTimePicker(
    selectedTime: LocalTime?,
    onTimeSelect: (LocalTime) -> Unit,
    onShowDialog: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime?.hour ?: 0,
        initialMinute = selectedTime?.minute ?: 0,
        is24Hour = true
    )
    CustomModalBottomSheet(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        sheetState = sheetState,
        onDismiss = {
            onShowDialog(false)
        }
    ) {
        TimePicker(
            state = timePickerState,
            colors = TimePickerDefaults.colors(
                clockDialColor = MaterialTheme.colorScheme.secondaryContainer,
                clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                selectorColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.background,

                timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,

                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )

        CustomButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            buttonTitle = LocalContext.current.getString(R.string.confirm),
            onClick = {
                onTimeSelect(LocalTime.of(timePickerState.hour, timePickerState.minute))
                onShowDialog(false)
            }
        )
    }
}