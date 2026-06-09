package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DaySelector(
    currentDate: LocalDate,
    dateEvent: LocalDate?,
    onSelectDateEvent: (LocalDate) -> Unit,
    focusManager: FocusManager
) {
    val firstDayOfWeek = remember {
        currentDate.getFirstDayOfWeek()
    }
    
    ColumnGroup(
        title = stringResource(R.string.day_of_week),
        withBackground = false,
        items = listOf {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                stringArrayResource(R.array.days_of_week).forEachIndexed { index, _ ->
                    val currentDate = firstDayOfWeek.plusDays(index.toLong())
                    CustomFilterChip(
                        title = currentDate.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ) ?: stringResource(R.string.not_specified),
                        imageVector = null,
                        selected = currentDate.dayOfWeek == dateEvent?.dayOfWeek,
                        onClick = {
                            onSelectDateEvent(currentDate)
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    )
}