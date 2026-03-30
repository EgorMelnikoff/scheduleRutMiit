package com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomCheckBox
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventViewModalDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    settingsViewModel: SettingsViewModel
) {
    CustomModalBottomSheet(
        modifier = Modifier.padding(horizontal = 16.dp),
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            items = listOf(
                {
                    ClickableItem(
                        title = stringResource(R.string.groups),
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.group),
                                contentDescription = stringResource(R.string.groups),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            CustomCheckBox(
                                checked = appSettings.eventView.groupsVisible,
                                onCheckedChange = { visible ->
                                    settingsViewModel.onSetEventGroupVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        settingsViewModel.onSetEventGroupVisibility(!appSettings.eventView.groupsVisible)
                    }
                }, {
                    ClickableItem(
                        title = stringResource(R.string.rooms),
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.room),
                                contentDescription = stringResource(R.string.rooms),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            CustomCheckBox(
                                checked = appSettings.eventView.roomsVisible,
                                onCheckedChange = { visible ->
                                    settingsViewModel.onSetEventRoomsVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        settingsViewModel.onSetEventRoomsVisibility(!appSettings.eventView.roomsVisible)
                    }
                }, {
                    ClickableItem(
                        title = stringResource(R.string.lecturers),
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.person),
                                contentDescription = stringResource(R.string.lecturers),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            CustomCheckBox(
                                checked = appSettings.eventView.lecturersVisible,
                                onCheckedChange = { visible ->
                                    settingsViewModel.onSetEventLecturersVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        settingsViewModel.onSetEventLecturersVisibility(!appSettings.eventView.lecturersVisible)
                    }
                }
            )
        )
        Spacer(modifier = Modifier.height(0.dp))
        ColumnGroup(
            items = listOf(
                {
                    ClickableItem(
                        title = stringResource(R.string.tag),
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.tag),
                                contentDescription = stringResource(R.string.tag),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            CustomCheckBox(
                                checked = appSettings.eventView.tagVisible,
                                onCheckedChange = { visible ->
                                    settingsViewModel.onSetEventTagVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        settingsViewModel.onSetEventTagVisibility(!appSettings.eventView.tagVisible)
                    }
                }, {
                    ClickableItem(
                        title = stringResource(R.string.comment),
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.comment),
                                contentDescription = stringResource(R.string.comment),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            CustomCheckBox(
                                checked = appSettings.eventView.commentVisible,
                                onCheckedChange = { visible ->
                                    settingsViewModel.onSetEventCommentVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        settingsViewModel.onSetEventCommentVisibility(!appSettings.eventView.commentVisible)
                    }
                }
            )
        )
    }
}

