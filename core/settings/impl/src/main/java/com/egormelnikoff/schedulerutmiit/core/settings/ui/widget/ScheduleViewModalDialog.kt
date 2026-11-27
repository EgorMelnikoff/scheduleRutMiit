package com.egormelnikoff.schedulerutmiit.core.settings.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R as CommonR
import com.egormelnikoff.schedulerutmiit.core.settings.R
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
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
fun ScheduleViewModalDialog(
    scheduleView: ScheduleView,
    onAction: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(
        modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
        showHandle = false,
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.settings_schedule_view),
            items = ScheduleView.entries.map {
                {
                    AppCell(
                        start = {
                            Icon(
                                icon = when (it) {
                                    ScheduleView.CALENDAR -> ImageVector.vectorResource(CommonR.drawable.calendar)
                                    ScheduleView.LIST -> ImageVector.vectorResource(CommonR.drawable.list)
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
                                            ScheduleView.CALENDAR -> stringResource(R.string.settings_calendar)
                                            ScheduleView.LIST -> stringResource(R.string.settings_full_list)
                                        },
                                    )
                                },
                                subtitle = {
                                    TextBlock(
                                        text = when (it) {
                                            ScheduleView.CALENDAR -> stringResource(R.string.settings_calendar_message)
                                            ScheduleView.LIST -> stringResource(R.string.settings_full_list_message)
                                        },
                                        maxLines = 2,
                                        typography = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colors.onSecondaryContainer,
                                    )
                                },

                            )
                        },
                        end = {
                            Custom {
                                RadioButton(
                                    selected = (it == scheduleView),
                                    onClick = {
                                        onAction(SettingsAction.OnSetScheduleView(it))
                                    }
                                )
                            }
                        },
                        showChevron = false,
                        onClick = {
                            onAction(
                                SettingsAction.OnSetScheduleView(it)
                            )
                        }
                    )
                }

            }
        )
    }
}