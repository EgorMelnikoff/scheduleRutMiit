package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.enums.Theme
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomFilterChip
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.DecorPreferences
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.ScheduleRutMiitTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenWidget
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.eventStateMock
import java.time.format.TextStyle

@Composable
fun EventNameWidget(
    timetableType: TimetableType,
    event: Event,
    horizontalPadding: Dp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = horizontalPadding
            ),
            text = event.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(
                    horizontal = horizontalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CustomFilterChip(
                imageVector = ImageVector.vectorResource(R.drawable.calendar),
                colors = FilterChipDefaults.filterChipColors(
                    disabledContainerColor = Color.Transparent,
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                ),
                border = BorderStroke(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                title = if (timetableType.isPeriodic()) {
                    val dayName = event.startDatetime.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        LocalLocale.current.platformLocale
                    ).toString().replaceFirstChar { it.uppercase() }
                    if (event.interval == 1) {
                        dayName
                    } else {
                        "$dayName, ${
                            stringResource(
                                R.string.week,
                                event.periodNumber ?: 0
                            ).replaceFirstChar { it.lowercase() }
                        }"
                    }
                } else "${
                    event.startDatetime.toLocalDate()
                        .format(dayMonthYearFormatter)
                }"
            )
            CustomFilterChip(
                imageVector = ImageVector.vectorResource(R.drawable.time),
                colors = FilterChipDefaults.filterChipColors(
                    disabledContainerColor = Color.Transparent,
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                ),
                border = BorderStroke(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                title = "${event.startDatetime.toLocalTimeWithTimeZone()} - ${event.endDatetime.toLocalTimeWithTimeZone()}"
            )

            event.typeName?.let { typeName ->
                CustomFilterChip(
                    title = typeName,
                    border = BorderStroke(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        disabledContainerColor = Color.Transparent,
                        disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}


@Preview
@Composable
fun EventScreenPreview() {
    PreviewLightTheme {
        EventNameWidget(
            eventStateMock.schedule.timetableType,
            eventStateMock.event,
            16.dp
        )
    }
}