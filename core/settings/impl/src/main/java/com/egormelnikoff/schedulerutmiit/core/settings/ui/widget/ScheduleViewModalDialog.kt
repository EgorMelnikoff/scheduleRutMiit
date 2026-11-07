package com.egormelnikoff.schedulerutmiit.core.settings.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsAction
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewModalDialog(
    scheduleView: ScheduleView,
    onAction: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(
        modifier = Modifier.padding(horizontal = 16.dp),
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.schedule_view),
            items = ScheduleView.entries.map {
                {
                    AppCell(
                        title = when (it) {
                            ScheduleView.CALENDAR -> stringResource(R.string.calendar)
                            ScheduleView.LIST -> stringResource(R.string.full_list)
                        },
                        subtitle = when (it) {
                            ScheduleView.CALENDAR -> stringResource(R.string.calendar_message)
                            ScheduleView.LIST -> stringResource(R.string.full_list_message)
                        },
                        subtitleMaxLines = 2,
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = when (it) {
                                    ScheduleView.CALENDAR -> ImageVector.vectorResource(R.drawable.calendar)
                                    ScheduleView.LIST -> ImageVector.vectorResource(R.drawable.list)
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            RadioButton(
                                selected = (it == scheduleView),
                                onClick = {
                                    onAction(SettingsAction.OnSetScheduleView(it))
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        onAction(SettingsAction.OnSetScheduleView(it))
                    }
                }

            }
        )
    }
}