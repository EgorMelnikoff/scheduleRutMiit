package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.widget

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.hourMinuteFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.utils.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.RoundedBox
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.HiddenEventsAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.eventMock
import java.time.format.TextStyle

@Composable
fun HiddenEventItemWidget(
    event: Event,
    onAction: (HiddenEventsAction) -> Unit
) {
    val locale = LocalLocale.current.platformLocale

    val startTime = remember(event) {
        event.startDatetime
            .toLocalTimeWithTimeZone()
            .format(hourMinuteFormatter)
    }

    val endTime = remember(event) {
        event.endDatetime
            .toLocalTimeWithTimeZone()
            .format(hourMinuteFormatter)
    }

    val day = remember(event) {
        if (event.interval != null) {
            event.startDatetime.dayOfWeek.getDisplayName(
                TextStyle.FULL,
                locale
            ).replaceFirstChar { c -> c.uppercase() }
        } else {
            event.startDatetime.format(dayMonthYearFormatter)
        }
    }
    RoundedBox {
        AppCell(
            title = event.name,
            titleMaxLines = 1,
            subtitle = buildString {
                event.typeName?.let {
                    append(it)
                }

                append(" ($day, $startTime - $endTime)")
            }.trim(),
            subtitleMaxLines = 2,
            showClickLabel = false,
            trailingIcon = {
                IconButton(
                    onClick = {
                        onAction(HiddenEventsAction.OnShowEvent(event.id))
                    },
                    colors = IconButtonDefaults.iconButtonColors()
                        .copy(
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = ImageVector.vectorResource(
                            R.drawable.visibility
                        ),
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@Preview(locale = "ru")
@Composable
fun HiddenEventItemWidgetPreview() {
    PreviewLightTheme{
        HiddenEventItemWidget(eventMock) { }
    }
}