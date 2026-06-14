package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.top_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.ui.platform.LocalLocale
import java.time.format.TextStyle

@Composable
fun CalendarTopBarItem(
    currentDate: LocalDate,
    isDisabled: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    selectDate: (LocalDate) -> Unit,
    badge: (@Composable () -> Unit)? = null
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Unspecified
    }
    val textColor = when {
        isDisabled -> MaterialTheme.colorScheme.outline
        isSelected -> MaterialTheme.colorScheme.onPrimary
        (currentDate.dayOfWeek.value == 7) -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = Modifier.defaultMinSize(
            minWidth = 40.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = backgroundColor
                )
                .let {
                    if (!isDisabled) {
                        it.clickable(
                            onClick = {
                                selectDate(currentDate)
                            }
                        )
                    } else {
                        it
                    }
                }
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = currentDate.dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    LocalLocale.current.platformLocale
                ).lowercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = currentDate.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
        badge?.invoke()
    }
}
