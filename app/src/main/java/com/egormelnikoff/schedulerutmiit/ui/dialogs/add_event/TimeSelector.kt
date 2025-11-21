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
import com.egormelnikoff.schedulerutmiit.ui.elements.RowGroup
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimeSelector(
    startTime: LocalTime?,
    endTime: LocalTime?,
    onShowDialogStartTime: (Boolean) -> Unit,
    onShowDialogEndTime: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    RowGroup(
        title = LocalContext.current.getString(R.string.time),
        items = listOf(
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
}