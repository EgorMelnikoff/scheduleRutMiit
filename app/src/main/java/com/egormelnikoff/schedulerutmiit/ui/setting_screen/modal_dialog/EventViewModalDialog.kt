package com.egormelnikoff.schedulerutmiit.ui.setting_screen.modal_dialog

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
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.components.check_box.AppCheckBox
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.common.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventViewModalDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    preferencesViewModel: PreferencesViewModel
) {
    AppBottomSheet(
        modifier = Modifier.padding(horizontal = 16.dp),
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            items = listOf(
                {
                    AppCell(
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
                            AppCheckBox(
                                checked = appSettings.eventView.groupsVisible,
                                onCheckedChange = { visible ->
                                    preferencesViewModel.onSetEventGroupVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        preferencesViewModel.onSetEventGroupVisibility(!appSettings.eventView.groupsVisible)
                    }
                }, {
                    AppCell(
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
                            AppCheckBox(
                                checked = appSettings.eventView.roomsVisible,
                                onCheckedChange = { visible ->
                                    preferencesViewModel.onSetEventRoomsVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        preferencesViewModel.onSetEventRoomsVisibility(!appSettings.eventView.roomsVisible)
                    }
                }, {
                    AppCell(
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
                            AppCheckBox(
                                checked = appSettings.eventView.lecturersVisible,
                                onCheckedChange = { visible ->
                                    preferencesViewModel.onSetEventLecturersVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        preferencesViewModel.onSetEventLecturersVisibility(!appSettings.eventView.lecturersVisible)
                    }
                }
            )
        )
        Spacer(modifier = Modifier.height(0.dp))
        ColumnGroup(
            items = listOf(
                {
                    AppCell(
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
                            AppCheckBox(
                                checked = appSettings.eventView.tagVisible,
                                onCheckedChange = { visible ->
                                    preferencesViewModel.onSetEventTagVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        preferencesViewModel.onSetEventTagVisibility(!appSettings.eventView.tagVisible)
                    }
                }, {
                    AppCell(
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
                            AppCheckBox(
                                checked = appSettings.eventView.commentVisible,
                                onCheckedChange = { visible ->
                                    preferencesViewModel.onSetEventCommentVisibility(visible)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        preferencesViewModel.onSetEventCommentVisibility(!appSettings.eventView.commentVisible)
                    }
                }
            )
        )
    }
}

