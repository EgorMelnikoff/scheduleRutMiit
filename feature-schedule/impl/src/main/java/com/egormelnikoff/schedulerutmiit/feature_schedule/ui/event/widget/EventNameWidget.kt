package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChipDefaults
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
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.ui.components.chip.AppFilterChip
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.eventStateMock
import java.time.format.TextStyle

@Composable
fun EventNameWidget(
    timetableType: TimetableType,
    event: Event,
    horizontalPadding: Dp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = horizontalPadding
            ),
            text = event.name,
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.onBackground,
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
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
        ) {
            AppFilterChip(
                imageVector = ImageVector.vectorResource(R.drawable.calendar),
                colors = FilterChipDefaults.filterChipColors(
                    disabledContainerColor = Color.Transparent,
                    disabledLabelColor = AppTheme.colors.onBackground,
                    disabledLeadingIconColor = AppTheme.colors.onBackground,
                ),
                border = BorderStroke(
                    width = 0.5.dp,
                    color = AppTheme.colors.outline
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
            AppFilterChip(
                imageVector = ImageVector.vectorResource(R.drawable.time),
                colors = FilterChipDefaults.filterChipColors(
                    disabledContainerColor = Color.Transparent,
                    disabledLabelColor = AppTheme.colors.onBackground,
                    disabledLeadingIconColor = AppTheme.colors.onBackground,
                ),
                border = BorderStroke(
                    width = 0.5.dp,
                    color = AppTheme.colors.outline
                ),
                title = "${event.startDatetime.toLocalTimeWithTimeZone()} - ${event.endDatetime.toLocalTimeWithTimeZone()}"
            )

            event.typeName?.let { typeName ->
                AppFilterChip(
                    title = typeName,
                    border = BorderStroke(
                        width = 0.5.dp,
                        color = AppTheme.colors.outline
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        disabledContainerColor = Color.Transparent,
                        disabledLabelColor = AppTheme.colors.onBackground,
                        disabledLeadingIconColor = AppTheme.colors.onBackground
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