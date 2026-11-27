package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.hidden_events.widget

import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.hourMinuteFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.CenterContent
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.IconButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.RoundedBox
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.hidden_events.HiddenEventsAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.eventMock
import java.time.format.TextStyle

@Composable
internal fun HiddenEventItemWidget(
    event: Event,
    onAction: (HiddenEventsAction) -> Unit
) {
    val locale = LocalLocale.current.platformLocale

    val startTime = remember(event) {
        event.startDatetime
            .toLocalTime()
            .format(hourMinuteFormatter)
    }

    val endTime = remember(event) {
        event.endDatetime
            .toLocalTime()
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
            showChevron = false,
            center = {
                CenterContent(
                    title = {
                        TextBlock(
                            text = event.name,
                            maxLines = 1,
                        )
                    },
                    subtitle = {
                        TextBlock(
                            text = buildString {
                                event.typeName?.let {
                                    append(it)
                                }

                                append(" ($day, $startTime - $endTime)")
                            }.trim(),
                            maxLines = 2,
                            typography = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.onSecondaryContainer,
                        )
                    },
                )
            },
            end = {
                IconButton(
                    icon = AppTheme.icons.visibility,
                    iconButtonColors = IconButtonDefaults.iconButtonColors().copy(
                        contentColor = AppTheme.colors.onSecondaryContainer
                    ),
                    iconSize = 24.dp
                ) {
                    onAction(HiddenEventsAction.OnShowEvent(event.id))
                }
            },
        )
    }
}

@Preview(locale = "ru")
@Composable
fun HiddenEventItemWidgetPreview() {
    PreviewLightTheme {
        HiddenEventItemWidget(eventMock) { }
    }
}