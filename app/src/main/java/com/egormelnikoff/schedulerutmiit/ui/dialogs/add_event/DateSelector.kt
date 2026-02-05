package com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomChip
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateSelector(
    dateEvent: LocalDate?,
    onSelectDateEvent: (LocalDate) -> Unit,
    focusManager: FocusManager
) {
    val firstDayOfWeek = LocalDate.now().getFirstDayOfWeek()
    ColumnGroup(
        title = stringResource(R.string.day_of_week),
        withBackground = false,
        items = listOf {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (date in 0L until 7L) {
                    val currentDate = firstDayOfWeek.plusDays(date)
                    CustomChip(
                        title = currentDate.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        )
                            ?: stringResource(R.string.not_specified),
                        imageVector = null,
                        selected = currentDate.dayOfWeek == dateEvent?.dayOfWeek,
                        onSelect = {
                            onSelectDateEvent(currentDate)
                            focusManager.clearFocus()
                        }
                    )
                }

            }
        }
    )
}