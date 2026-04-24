package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.hourMinuteFormatter
import java.time.LocalTime

@Composable
fun TimeSelector(
    startTime: LocalTime?,
    endTime: LocalTime?,
    onShowDialogStartTime: (Boolean) -> Unit,
    onShowDialogEndTime: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    RowGroup(
        title = stringResource(R.string.time),
        items = listOf(
            {
                ChooseDateTimeButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = startTime?.format(hourMinuteFormatter)
                        ?: stringResource(R.string.start_time),
                    imageVector = ImageVector.vectorResource(R.drawable.time)
                ) {
                    onShowDialogStartTime(true)
                    focusManager.clearFocus()
                }
            }, {
                ChooseDateTimeButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = endTime?.format(hourMinuteFormatter)
                        ?: stringResource(R.string.end_time),
                    imageVector = ImageVector.vectorResource(R.drawable.time),
                    enabled = startTime != null
                ) {
                    onShowDialogEndTime(true)
                    focusManager.clearFocus()
                }
            }
        )
    )
}