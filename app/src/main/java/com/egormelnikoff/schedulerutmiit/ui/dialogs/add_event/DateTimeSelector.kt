package com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.ChooseDateTimeButton
import com.egormelnikoff.schedulerutmiit.ui.elements.GridGroup
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
        title = LocalContext.current.getString(R.string.date_and_time),
        items = listOf(
            listOf {
                ChooseDateTimeButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = dateEvent?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        ?: LocalContext.current.getString(R.string.date),
                    onClick = {
                        onShowDialogDate(true)
                        focusManager.clearFocus()
                    }
                )
            },
            listOf(
                {
                    ChooseDateTimeButton(
                        modifier = Modifier.fillMaxWidth(),
                        title = startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            ?: LocalContext.current.getString(R.string.start_time),
                        imageVector = ImageVector.vectorResource(R.drawable.time),
                        onClick = {
                            onShowDialogStartTime(true)
                            focusManager.clearFocus()
                        }
                    )
                }, {
                    ChooseDateTimeButton(
                        modifier = Modifier.fillMaxWidth(),
                        title = endTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            ?: LocalContext.current.getString(R.string.end_time),
                        imageVector = ImageVector.vectorResource(R.drawable.time),
                        onClick = {
                            onShowDialogEndTime(true)
                            focusManager.clearFocus()
                        },
                        enabled = startTime != null
                    )
                }
            )
        )
    )
}