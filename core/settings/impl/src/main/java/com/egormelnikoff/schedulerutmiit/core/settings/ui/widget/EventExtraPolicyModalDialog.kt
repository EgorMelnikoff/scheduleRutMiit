package com.egormelnikoff.schedulerutmiit.core.settings.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsAction
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
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
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.comments_and_tags),
            items = EventExtraPolicy.entries.map {
                {
                    AppCell(
                        title = when (it) {
                            EventExtraPolicy.DEFAULT -> stringResource(R.string.by_default)
                            EventExtraPolicy.SYNCHRONIZED -> stringResource(R.string._synchronized)
                            EventExtraPolicy.BY_DATES -> stringResource(R.string.by_dates)
                        },
                        subtitle = when (it) {
                            EventExtraPolicy.SYNCHRONIZED -> stringResource(R.string.sync_tag_comments_message)
                            EventExtraPolicy.BY_DATES -> stringResource(R.string.by_dates_message)
                            EventExtraPolicy.DEFAULT -> null
                        },
                        subtitleMaxLines = 2,
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = when (it) {
                                    EventExtraPolicy.SYNCHRONIZED -> ImageVector.vectorResource(R.drawable.sync)
                                    EventExtraPolicy.BY_DATES -> ImageVector.vectorResource(R.drawable.calendar)
                                    EventExtraPolicy.DEFAULT -> ImageVector.vectorResource(R.drawable.split)
                                },
                                contentDescription = null,
                                tint = AppTheme.colors.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            RadioButton(
                                selected = (it == eventExtraPolicy),
                                onClick = {
                                    onAction(
                                        SettingsAction.OnSetEventExtraPolicy(it)
                                    )
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        onAction(
                            SettingsAction.OnSetEventExtraPolicy(it)
                        )
                    }
                }

            }
        )
    }
}