package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = {
            onShowDialog(false)
        },
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.outline
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = MaterialTheme.colorScheme.surface,
                    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    selectorColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.background,

                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,

                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            CustomButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = LocalContext.current.getString(R.string.confirm),
                onClick = {
                    onTimeSelect(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    onShowDialog(false)
                }
            )
        }
    }
}