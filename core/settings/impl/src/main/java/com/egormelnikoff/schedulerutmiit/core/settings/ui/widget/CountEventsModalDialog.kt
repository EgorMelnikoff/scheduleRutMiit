package com.egormelnikoff.schedulerutmiit.core.settings.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.settings.R
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsAction
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.CenterContent
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Custom
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountEventsModalDialog(
    eventsCountView: EventsCountView,
    onAction: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(
        modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
        showHandle = false,
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.settings_show_count_classes),
            items = EventsCountView.entries.map {
                {
                    AppCell(
                        start = {
                            Icon(
                                icon = when (it) {
                                    EventsCountView.DETAILS -> AppTheme.icons.points
                                    EventsCountView.BRIEFLY -> AppTheme.icons.one
                                    EventsCountView.OFF -> AppTheme.icons.visibilityOff
                                },
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onSecondaryContainer
                            )
                        },
                        center = {
                            CenterContent(
                                title = {
                                    TextBlock(
                                        text = when (it) {
                                            EventsCountView.DETAILS -> stringResource(R.string.settings_details)
                                            EventsCountView.BRIEFLY -> stringResource(R.string.settings_briefly)
                                            EventsCountView.OFF -> stringResource(R.string.settings_dont_show)
                                        }
                                    )
                                },
                                subtitle = {
                                    TextBlock(
                                        text = when (it) {
                                            EventsCountView.DETAILS -> stringResource(R.string.settings_display_each_lesson)
                                            EventsCountView.BRIEFLY -> stringResource(R.string.settings_display_number_lessons)
                                            EventsCountView.OFF -> null
                                        },
                                        typography = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colors.onSecondaryContainer,
                                    )
                                }
                            )
                        },
                        end = {
                            Custom {
                                RadioButton(
                                    selected = (it == eventsCountView),
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetEventsCountView(it)
                                        )
                                    }
                                )
                            }
                        },
                        showChevron = false,
                        onClick = {
                            onAction(
                                SettingsAction.OnSetEventsCountView(it)
                            )
                        }
                    )
                }

            }
        )
    }
}