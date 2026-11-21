package com.egormelnikoff.schedulerutmiit.core.ui.components.time_picker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePicker(
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
    AppBottomSheet(
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
                clockDialColor = AppTheme.colors.secondaryContainer,
                clockDialSelectedContentColor = AppTheme.colors.onPrimary,
                clockDialUnselectedContentColor = AppTheme.colors.onSecondaryContainer,
                selectorColor = AppTheme.colors.primary,
                containerColor = AppTheme.colors.background,

                timeSelectorSelectedContainerColor = AppTheme.colors.primary,
                timeSelectorSelectedContentColor = AppTheme.colors.onPrimary,

                timeSelectorUnselectedContainerColor = AppTheme.colors.secondaryContainer,
                timeSelectorUnselectedContentColor = AppTheme.colors.onSecondaryContainer
            )
        )

        AppButton(
            modifier = Modifier.fillMaxWidth().padding(horizontal = AppTheme.paddings.padding16),
            buttonTitle = stringResource(R.string.confirm),
            onClick = {
                onTimeSelect(LocalTime.of(timePickerState.hour, timePickerState.minute))
                onShowDialog(false)
            }
        )
    }
}

@Preview
@Composable
fun AppTimePickerPreview() {
    PreviewLightTheme {
        AppTimePicker(
            selectedTime = LocalTime.of(9, 41),
            onTimeSelect = {},
            onShowDialog = {}
        )
    }
}