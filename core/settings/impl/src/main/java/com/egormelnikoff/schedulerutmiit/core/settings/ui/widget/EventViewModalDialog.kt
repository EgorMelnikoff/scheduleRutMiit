package com.egormelnikoff.schedulerutmiit.core.settings.ui.widget

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
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsAction
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.check_box.AppCheckBox
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventViewModalDialog(
    eventView: EventView,
    onAction: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
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
                                checked = eventView.groupsVisible,
                                onCheckedChange = { visible ->
                                    onAction(
                                        SettingsAction.OnSetEventGroupsVisibility(visible)
                                    )
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        onAction(
                            SettingsAction.OnSetEventGroupsVisibility(!eventView.groupsVisible)
                        )
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
                                checked = eventView.roomsVisible,
                                onCheckedChange = { visible ->
                                    onAction(
                                        SettingsAction.OnSetEventRoomsVisibility(visible)
                                    )
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        onAction(
                            SettingsAction.OnSetEventRoomsVisibility(!eventView.roomsVisible)
                        )
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
                                checked = eventView.lecturersVisible,
                                onCheckedChange = { visible ->
                                    onAction(
                                        SettingsAction.OnSetEventLecturersVisibility(visible)
                                    )
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        onAction(
                            SettingsAction.OnSetEventLecturersVisibility(!eventView.lecturersVisible)
                        )
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
                                checked = eventView.tagVisible,
                                onCheckedChange = { visible ->
                                    onAction(
                                        SettingsAction.OnSetEventTagVisibility(visible)
                                    )
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        onAction(
                            SettingsAction.OnSetEventTagVisibility(!eventView.tagVisible)
                        )
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
                                checked = eventView.commentVisible,
                                onCheckedChange = { visible ->
                                    onAction(
                                        SettingsAction.OnSetEventCommentVisibility(visible)
                                    )
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        onAction(
                            SettingsAction.OnSetEventCommentVisibility(!eventView.commentVisible)
                        )
                    }
                }
            )
        )
    }
}

