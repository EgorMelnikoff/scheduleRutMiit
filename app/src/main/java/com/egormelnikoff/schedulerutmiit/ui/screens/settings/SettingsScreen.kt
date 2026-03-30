package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.enums.Theme
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSwitch
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog.CountEventsModalDialog
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog.EventViewModalDialog
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog.InfoModalDialog
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog.ThemeModalDialog
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

data class ThemeSelectorItemContent(
    val theme: Theme,
    val imageVector: ImageVector?,
    val displayedName: String
)

@Composable
fun SettingsScreen(
    appUiState: AppUiState,
    appSettings: AppSettings,
    settingsViewModel: SettingsViewModel,
    externalPadding: PaddingValues
) {
    var decorDialog by remember { mutableStateOf(false) }
    var eventViewDialog by remember { mutableStateOf(false) }
    var countEventsDialog by remember { mutableStateOf(false) }
    var infoDialog by remember { mutableStateOf(false) }

    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = externalPadding.calculateTopPadding() + 16.dp,
            bottom = externalPadding.calculateBottomPadding()
        ),
        state = appUiState.settingsListState
    ) {
        item {
            val data = mutableListOf<String>()
            if (appSettings.eventView.groupsVisible) {
                data.add(stringResource(R.string.groups))
            }

            if (appSettings.eventView.roomsVisible) {
                data.add(stringResource(R.string.rooms))
            }

            if (appSettings.eventView.lecturersVisible) {
                data.add(stringResource(R.string.lecturers))
            }

            if (appSettings.eventView.tagVisible) {
                data.add(stringResource(R.string.tag))
            }

            if (appSettings.eventView.commentVisible) {
                data.add(stringResource(R.string.comment))
            }


            ColumnGroup(
                title = stringResource(R.string.schedule),
                items = listOf(
                    {
                        ClickableItem(
                            title = stringResource(R.string.event_view),
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.event_view),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            subtitle = data
                                .joinToString(", ")
                                .lowercase()
                                .replaceFirstChar { it.uppercase() },
                            subtitleMaxLines = 2,
                            defaultMinHeight = 36.dp
                        ) {
                            eventViewDialog = true
                        }
                    }, {
                        ClickableItem(
                            title = stringResource(R.string.show_count_classes),
                            subtitle = when (appSettings.eventsCountView) {
                                EventsCountView.DETAILS -> stringResource(R.string.details)
                                EventsCountView.BRIEFLY -> stringResource(R.string.briefly)
                                EventsCountView.OFF -> stringResource(R.string.off)
                            },
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = when (appSettings.eventsCountView) {
                                        EventsCountView.DETAILS -> ImageVector.vectorResource(R.drawable.points)
                                        EventsCountView.BRIEFLY -> ImageVector.vectorResource(R.drawable.one)
                                        EventsCountView.OFF -> ImageVector.vectorResource(R.drawable.visibility_off)
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            defaultMinHeight = 36.dp
                        ) {
                            countEventsDialog = true
                        }
                    }, {
                        ClickableItem(
                            title = stringResource(R.string.not_delete_schedules),
                            subtitle = stringResource(R.string.not_delete_schedules_message),
                            subtitleMaxLines = 3,
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.undelete),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            defaultMinHeight = 36.dp,
                            trailingIcon = {
                                CustomSwitch(
                                    checked = !appSettings.schedulesDeletable
                                ) {
                                    settingsViewModel.onSetSchedulesDeletable(!it)
                                }
                            },
                            showClickLabel = false
                        ) {
                            settingsViewModel.onSetSchedulesDeletable(!appSettings.schedulesDeletable)
                        }
                    }, {
                        ClickableItem(
                            title = stringResource(R.string.sync_tag_comments),
                            subtitle = stringResource(R.string.sync_tag_comments_message),
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.sync),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            subtitleMaxLines = 3,
                            defaultMinHeight = 36.dp,
                            trailingIcon = {
                                CustomSwitch(
                                    checked = appSettings.syncTagsAndComments
                                ) {
                                    settingsViewModel.onSetSyncTagsComments(it)
                                }
                            },
                            showClickLabel = false
                        ) {
                            settingsViewModel.onSetSyncTagsComments(!appSettings.syncTagsAndComments)
                        }
                    }
                )
            )
        }

        item {
            ColumnGroup(
                title = stringResource(R.string.general),
                items = listOf(
                    {
                        ClickableItem(
                            title = stringResource(R.string.theme),
                            subtitle = when (appSettings.theme) {
                                Theme.LIGHT -> stringResource(R.string.light)
                                Theme.DARK -> stringResource(R.string.dark)
                                Theme.SYSTEM -> stringResource(R.string.auto)
                            },
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = if (appSettings.theme.isDarkTheme()) ImageVector.vectorResource(
                                        R.drawable.moon
                                    )
                                    else ImageVector.vectorResource(R.drawable.sun),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            defaultMinHeight = 36.dp
                        ) {
                            decorDialog = true
                        }
                    }, {
                        ClickableItem(
                            title = stringResource(R.string.about_app),
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.info),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            defaultMinHeight = 36.dp
                        ) {
                            infoDialog = true
                        }
                    }
                )
            )
        }
    }
    StatusBarProtection()

    if (eventViewDialog) {
        EventViewModalDialog(
            onDismiss = {
                eventViewDialog = false
            },
            appSettings = appSettings,
            settingsViewModel = settingsViewModel
        )
    }

    if (decorDialog) {
        ThemeModalDialog(
            onDismiss = {
                decorDialog = false
            },
            appSettings = appSettings,
            settingsViewModel = settingsViewModel
        )
    }

    if (countEventsDialog) {
        CountEventsModalDialog(
            {
                countEventsDialog = false
            }, appSettings, settingsViewModel
        )
    }

    if (infoDialog) {
        InfoModalDialog {
            infoDialog = false
        }
    }
}