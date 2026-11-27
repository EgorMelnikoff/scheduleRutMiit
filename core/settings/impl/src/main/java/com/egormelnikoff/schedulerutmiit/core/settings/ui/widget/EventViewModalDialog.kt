package com.egormelnikoff.schedulerutmiit.core.settings.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.settings.R
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsAction
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Custom
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.components.check_box.AppCheckBox
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventViewModalDialog(
    eventView: EventView,
    onAction: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(
        modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
        showHandle = false,
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            items = listOf(
                {
                    AppCell(
                        start = {
                            Icon(
                                icon = AppTheme.icons.group,
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onSecondaryContainer
                            )
                        },
                        center = {
                            TextBlock(
                                text = stringResource(R.string.settings_groups),
                            )
                        },
                        end = {
                            Custom {
                                AppCheckBox(
                                    checked = eventView.groupsVisible,
                                    onCheckedChange = { visible ->
                                        onAction(
                                            SettingsAction.OnSetEventGroupsVisibility(visible)
                                        )
                                    }
                                )
                            }
                        },
                        showChevron = false,
                        onClick = {
                            onAction(
                                SettingsAction.OnSetEventGroupsVisibility(!eventView.groupsVisible)
                            )
                        }
                    )
                }, {
                    AppCell(
                        start = {
                            Icon(
                                icon = AppTheme.icons.room,
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onSecondaryContainer
                            )
                        },
                        center = {
                            TextBlock(
                                text = stringResource(R.string.settings_rooms),
                            )
                        },
                        end = {
                            Custom {
                                AppCheckBox(
                                    checked = eventView.roomsVisible,
                                    onCheckedChange = { visible ->
                                        onAction(
                                            SettingsAction.OnSetEventRoomsVisibility(visible)
                                        )
                                    }
                                )
                            }
                        },
                        showChevron = false,
                        onClick = {
                            onAction(
                                SettingsAction.OnSetEventRoomsVisibility(!eventView.roomsVisible)
                            )
                        }
                    )
                }, {
                    AppCell(
                        start = {
                            Icon(
                                icon = AppTheme.icons.person,
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onSecondaryContainer
                            )
                        },
                        center = {
                            TextBlock(
                                text = stringResource(R.string.settings_lecturers),
                            )
                        },
                        end = {
                            Custom {
                                AppCheckBox(
                                    checked = eventView.lecturersVisible,
                                    onCheckedChange = { visible ->
                                        onAction(
                                            SettingsAction.OnSetEventLecturersVisibility(visible)
                                        )
                                    }
                                )
                            }
                        },
                        showChevron = false,
                        onClick = {
                            onAction(
                                SettingsAction.OnSetEventLecturersVisibility(!eventView.lecturersVisible)
                            )
                        }
                    )
                }
            )
        )
        SpacerHeight(0.dp)
        ColumnGroup(
            items = listOf(
                {
                    AppCell(
                        start = {
                            Icon(
                                icon = AppTheme.icons.tag,
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onSecondaryContainer
                            )
                        },
                        center = {
                            TextBlock(
                                text = stringResource(R.string.settings_tag),
                            )
                        },
                        end = {
                            Custom {
                                AppCheckBox(
                                    checked = eventView.tagVisible,
                                    onCheckedChange = { visible ->
                                        onAction(
                                            SettingsAction.OnSetEventTagVisibility(visible)
                                        )
                                    }
                                )
                            }
                        },
                        showChevron = false,
                        onClick = {
                            onAction(
                                SettingsAction.OnSetEventTagVisibility(!eventView.tagVisible)
                            )
                        }
                    )
                }, {
                    AppCell(
                        start = {
                            Icon(
                                icon = AppTheme.icons.comment,
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onSecondaryContainer
                            )
                        },
                        center = {
                            TextBlock(
                                text = stringResource(R.string.settings_comment),
                            )
                        },
                        end = {
                            Custom {
                                AppCheckBox(
                                    checked = eventView.commentVisible,
                                    onCheckedChange = { visible ->
                                        onAction(
                                            SettingsAction.OnSetEventCommentVisibility(visible)
                                        )
                                    }
                                )
                            }
                        },
                        showChevron = false,
                        onClick = {
                            onAction(
                                SettingsAction.OnSetEventCommentVisibility(!eventView.commentVisible)
                            )
                        }
                    )
                }
            )
        )
    }
}

