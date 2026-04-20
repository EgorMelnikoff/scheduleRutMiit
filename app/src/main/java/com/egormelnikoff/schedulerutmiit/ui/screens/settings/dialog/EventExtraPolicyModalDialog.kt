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
import com.egormelnikoff.schedulerutmiit.app.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.data.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.ui.view_models.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventExtraPolicyModalDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    settingsViewModel: SettingsViewModel
) {
    CustomModalBottomSheet(
        modifier = Modifier.padding(horizontal = 16.dp),
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.comments_and_tags),
            items = EventExtraPolicy.entries.map {
                {
                    ClickableItem(
                        title = when (it) {
                            EventExtraPolicy.DEFAULT -> stringResource(R.string.by_default)
                            EventExtraPolicy.SYNCHRONIZED -> stringResource(R.string._synchronized)
                            EventExtraPolicy.BY_DATES -> stringResource(R.string.by_dates)
                        },
                        subtitle =  when (it) {
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
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            RadioButton(
                                selected = (it == appSettings.eventExtraPolicy),
                                onClick = {
                                    settingsViewModel.onSetEventExtraPolicy(it)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        settingsViewModel.onSetEventExtraPolicy(it)
                    }
                }

            }
        )
    }
}