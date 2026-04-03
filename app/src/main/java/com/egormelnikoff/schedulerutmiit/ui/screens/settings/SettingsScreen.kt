package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.derivedStateOf
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
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.enums.Theme
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSwitch
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog.CountEventsModalDialog
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog.EventViewModalDialog
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog.InfoModalDialog
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog.ScheduleViewModalDialog
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

sealed interface SettingsDialog {
    object ScheduleView : SettingsDialog
    object EventView : SettingsDialog
    object CountEvents : SettingsDialog
    object Decor : SettingsDialog
    object Info : SettingsDialog
}

@Composable
fun SettingsScreen(
    appUiState: AppUiState,
    appSettings: AppSettings,
    settingsViewModel: SettingsViewModel,
    externalPadding: PaddingValues
) {
    var activeDialog by remember { mutableStateOf<SettingsDialog?>(null) }

    val visibleSettingsIds by remember(appSettings.eventView) {
        derivedStateOf {
            buildList {
                if (appSettings.eventView.groupsVisible) add(R.string.groups)
                if (appSettings.eventView.roomsVisible) add(R.string.rooms)
                if (appSettings.eventView.lecturersVisible) add(R.string.lecturers)
                if (appSettings.eventView.tagVisible) add(R.string.tag)
                if (appSettings.eventView.commentVisible) add(R.string.comment)
            }
        }
    }

    val visibleSettings = visibleSettingsIds.map { stringResource(it) }

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
            ColumnGroup(
                title = stringResource(R.string.schedule),
                items = listOf(
                    {
                        ClickableItem(
                            title = stringResource(R.string.schedule_view),
                            leadingIcon = {
                                AnimatedContent(
                                    targetState = appSettings.scheduleView,
                                    transitionSpec = {
                                        scaleIn() togetherWith scaleOut()
                                    }
                                ) { state ->
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = when (state) {
                                            ScheduleView.CALENDAR -> ImageVector.vectorResource(R.drawable.calendar)
                                            ScheduleView.SPLIT_WEEKS -> ImageVector.vectorResource(R.drawable.split)
                                            ScheduleView.LIST -> ImageVector.vectorResource(R.drawable.list)
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            },
                            subtitle = when (appSettings.scheduleView) {
                                ScheduleView.CALENDAR -> stringResource(R.string.calendar)
                                ScheduleView.SPLIT_WEEKS -> stringResource(R.string.by_weeks)
                                ScheduleView.LIST -> stringResource(R.string.full_list)
                            },
                            defaultMinHeight = 36.dp
                        ) {
                            activeDialog = SettingsDialog.ScheduleView
                        }
                    }, {
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
                            subtitle = visibleSettings
                                .joinToString(", ")
                                .lowercase()
                                .replaceFirstChar { it.uppercase() },
                            subtitleMaxLines = 1,
                            defaultMinHeight = 36.dp
                        ) {
                            activeDialog = SettingsDialog.EventView
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
                                AnimatedContent(
                                    targetState = appSettings.eventsCountView,
                                    transitionSpec = {
                                        scaleIn() togetherWith scaleOut()
                                    }
                                ) { state ->
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = when (state) {
                                            EventsCountView.DETAILS -> ImageVector.vectorResource(R.drawable.points)
                                            EventsCountView.BRIEFLY -> ImageVector.vectorResource(R.drawable.one)
                                            EventsCountView.OFF -> ImageVector.vectorResource(R.drawable.visibility_off)
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            },
                            defaultMinHeight = 36.dp
                        ) {
                            activeDialog = SettingsDialog.CountEvents
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
                            subtitle = when (appSettings.decorPreferences.theme) {
                                Theme.LIGHT -> stringResource(R.string.light)
                                Theme.DARK -> stringResource(R.string.dark)
                                Theme.SYSTEM -> stringResource(R.string.auto)
                            },
                            leadingIcon = {
                                AnimatedContent(
                                    targetState = appSettings.decorPreferences.theme.isDarkTheme(),
                                    transitionSpec = {
                                        scaleIn() togetherWith scaleOut()
                                    }
                                ) { state ->
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = if (state) {
                                            ImageVector.vectorResource(R.drawable.moon)
                                        } else {
                                            ImageVector.vectorResource(R.drawable.sun)
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            },
                            defaultMinHeight = 36.dp
                        ) {
                            activeDialog = SettingsDialog.Decor
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
                            activeDialog = SettingsDialog.Info
                        }
                    }
                )
            )
        }
    }
    StatusBarProtection()

    when (activeDialog) {
        is SettingsDialog.ScheduleView -> {
            ScheduleViewModalDialog(
                onDismiss = {
                    activeDialog = null
                },
                appSettings = appSettings,
                settingsViewModel = settingsViewModel
            )
        }

        is SettingsDialog.EventView -> {
            EventViewModalDialog(
                onDismiss = {
                    activeDialog = null
                },
                appSettings = appSettings,
                settingsViewModel = settingsViewModel
            )
        }

        is SettingsDialog.Info -> {
            InfoModalDialog {
                activeDialog = null
            }
        }

        is SettingsDialog.Decor -> {
            ThemeModalDialog(
                onDismiss = {
                    activeDialog = null
                },
                appSettings = appSettings,
                settingsViewModel = settingsViewModel
            )
        }

        is SettingsDialog.CountEvents -> {
            CountEventsModalDialog(
                {
                    activeDialog = null
                }, appSettings, settingsViewModel
            )
        }

        else -> {}
    }
}