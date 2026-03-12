package com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.hourMinuteFormatter
import com.egormelnikoff.schedulerutmiit.ui.elements.ChooseDateTimeButton
import com.egormelnikoff.schedulerutmiit.ui.elements.GridGroup
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun DateTimeSelector(
    dateEvent: LocalDate?,
    startTime: LocalTime?,
    endTime: LocalTime?,
    onShowDialogDate: (Boolean) -> Unit,
    onShowDialogStartTime: (Boolean) -> Unit,
    onShowDialogEndTime: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    GridGroup(
        title = stringResource(R.string.date_and_time),
        items = listOf(
            listOf {
                ChooseDateTimeButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = dateEvent?.format(dayMonthYearFormatter)
                        ?: stringResource(R.string.date)
                ) {
                    onShowDialogDate(true)
                    focusManager.clearFocus()
                }
            },
            listOf(
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
    )
}