package com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog

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
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewModalDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    settingsViewModel: SettingsViewModel
) {
    CustomModalBottomSheet(
        modifier = Modifier.padding(horizontal = 16.dp),
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = "Вид расписания",
            items = ScheduleView.entries.map {
                {
                    ClickableItem(
                        title = when (it) {
                            ScheduleView.CALENDAR -> stringResource(R.string.calendar)
                            ScheduleView.SPLIT_WEEKS -> stringResource(R.string.by_weeks)
                            ScheduleView.LIST -> stringResource(R.string.full_list)
                        },
                        subtitle = when (it) {
                            ScheduleView.CALENDAR -> stringResource(R.string.calendar_message)
                            ScheduleView.SPLIT_WEEKS -> stringResource(R.string.by_weeks_message)
                            ScheduleView.LIST -> stringResource(R.string.full_list_message)
                        },
                        subtitleMaxLines = 2,
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = when (it) {
                                    ScheduleView.CALENDAR -> ImageVector.vectorResource(R.drawable.calendar)
                                    ScheduleView.SPLIT_WEEKS -> ImageVector.vectorResource(R.drawable.split)
                                    ScheduleView.LIST -> ImageVector.vectorResource(R.drawable.list)
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            RadioButton(
                                selected = (it == appSettings.scheduleView),
                                onClick = {
                                    settingsViewModel.onSetScheduleView(it)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        settingsViewModel.onSetScheduleView(it)
                    }
                }

            }
        )
    }
}