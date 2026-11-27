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
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
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
fun EventExtraPolicyModalDialog(
    eventExtraPolicy: EventExtraPolicy,
    onAction: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(
        modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
        showHandle = false,
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.settings_comments_and_tags),
            items = EventExtraPolicy.entries.map {
                {
                    AppCell(
                        start = {
                            Icon(
                                icon = when (it) {
                                    EventExtraPolicy.SYNCHRONIZED -> ImageVector.vectorResource(CommonR.drawable.sync)
                                    EventExtraPolicy.BY_DATES -> ImageVector.vectorResource(CommonR.drawable.calendar)
                                    EventExtraPolicy.DEFAULT -> ImageVector.vectorResource(CommonR.drawable.split)
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
                                            EventExtraPolicy.DEFAULT -> stringResource(R.string.settings_by_default)
                                            EventExtraPolicy.SYNCHRONIZED -> stringResource(R.string.settings_synchronized)
                                            EventExtraPolicy.BY_DATES -> stringResource(R.string.settings_by_dates)
                                        }
                                    )
                                },
                                subtitle = {
                                    TextBlock(
                                        text = when (it) {
                                            EventExtraPolicy.SYNCHRONIZED -> stringResource(R.string.settings_sync_tag_comments_message)
                                            EventExtraPolicy.BY_DATES -> stringResource(R.string.settings_by_dates_message)
                                            EventExtraPolicy.DEFAULT -> null
                                        },
                                        maxLines = 2,
                                        typography = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colors.onSecondaryContainer,
                                    )
                                }
                            )
                        },
                        end = {
                            Custom {
                                RadioButton(
                                    selected = (it == eventExtraPolicy),
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetEventExtraPolicy(it)
                                        )
                                    }
                                )
                            }
                        },
                        showChevron = false,
                        onClick = {
                            onAction(
                                SettingsAction.OnSetEventExtraPolicy(it)
                            )
                        }
                    )
                }

            }
        )
    }
}