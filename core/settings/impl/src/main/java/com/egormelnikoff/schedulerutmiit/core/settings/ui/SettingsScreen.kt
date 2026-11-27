package com.egormelnikoff.schedulerutmiit.core.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.settings.R
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.Theme
import com.egormelnikoff.schedulerutmiit.core.settings.ui.widget.CountEventsModalDialog
import com.egormelnikoff.schedulerutmiit.core.settings.ui.widget.EventExtraPolicyModalDialog
import com.egormelnikoff.schedulerutmiit.core.settings.ui.widget.EventViewModalDialog
import com.egormelnikoff.schedulerutmiit.core.settings.ui.widget.InfoModalDialog
import com.egormelnikoff.schedulerutmiit.core.settings.ui.widget.ScheduleViewModalDialog
import com.egormelnikoff.schedulerutmiit.core.settings.ui.widget.ThemeModalDialog
import com.egormelnikoff.schedulerutmiit.core.ui.components._switch.AppSwitch
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AnimatedIcon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.CenterContent
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Custom
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.LoadingScreen

sealed interface SettingsDialog {
    data object ScheduleView : SettingsDialog
    data object EventView : SettingsDialog
    data object CountEvents : SettingsDialog
    data object Decor : SettingsDialog
    data object Info : SettingsDialog
    data object EventExtraPolicy : SettingsDialog
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    Scaffold { contentPadding ->
        if (state.appSettings == null) {
            LoadingScreen()
        } else {
            val settingsListState = rememberLazyStaggeredGridState()
            val visibleSettingsIds by remember(state.appSettings.eventView) {
                derivedStateOf {
                    buildList {
                        if (state.appSettings.eventView.groupsVisible) add(R.string.settings_groups)
                        if (state.appSettings.eventView.roomsVisible) add(R.string.settings_rooms)
                        if (state.appSettings.eventView.lecturersVisible) add(R.string.settings_lecturers)
                        if (state.appSettings.eventView.tagVisible) add(R.string.settings_tag)
                        if (state.appSettings.eventView.commentVisible) add(R.string.settings_comment)
                    }
                }
            }

            val visibleSettings = visibleSettingsIds.map { stringResource(it) }

            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.background),
                columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
                verticalItemSpacing = AppTheme.paddings.padding16,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding16),
                contentPadding = PaddingValues(
                    start = AppTheme.paddings.padding16,
                    end = AppTheme.paddings.padding16,
                    top = contentPadding.calculateTopPadding() + AppTheme.paddings.padding16,
                    bottom = contentPadding.calculateBottomPadding()
                ),
                state = settingsListState
            ) {
                item {
                    ColumnGroup(
                        title = stringResource(R.string.settings_interface),
                        items = listOf(
                            {
                                AppCell(
                                    start = {
                                        AnimatedIcon(
                                            targetState = state.appSettings.scheduleView,
                                            iconProvider = { state ->
                                                when (state) {
                                                    ScheduleView.CALENDAR -> {
                                                        AppTheme.icons.calendar
                                                    }

                                                    ScheduleView.LIST -> {
                                                        AppTheme.icons.list
                                                    }
                                                }
                                            }
                                        )
                                    },
                                    center = {
                                        CenterContent(
                                            title = {
                                                TextBlock(
                                                    text = stringResource(R.string.settings_schedule_view),
                                                )
                                            },
                                            subtitle = {
                                                TextBlock(
                                                    text = when (state.appSettings.scheduleView) {
                                                        ScheduleView.CALENDAR -> stringResource(R.string.settings_calendar)
                                                        ScheduleView.LIST -> stringResource(R.string.settings_full_list)
                                                    },
                                                    typography = AppTheme.typography.bodyMedium,
                                                    color = AppTheme.colors.onSecondaryContainer,
                                                )
                                            },
                                        )
                                    },
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetActiveDialog(
                                                SettingsDialog.ScheduleView
                                            )
                                        )
                                    }
                                )
                            }, {
                                AppCell(
                                    start = {
                                        Icon(
                                            icon = AppTheme.icons.eventView,
                                            iconSize = 20.dp,
                                            iconColor = AppTheme.colors.onSecondaryContainer
                                        )
                                    },
                                    center = {
                                        CenterContent(
                                            title = {
                                                TextBlock(
                                                    text = stringResource(R.string.settings_event_view)
                                                )
                                            },
                                            subtitle = {
                                                TextBlock(
                                                    text = visibleSettings
                                                        .joinToString(", ")
                                                        .lowercase()
                                                        .replaceFirstChar { it.uppercase() },
                                                    maxLines = 1,
                                                    typography = AppTheme.typography.bodyMedium,
                                                    color = AppTheme.colors.onSecondaryContainer,
                                                )
                                            }
                                        )
                                    },
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetActiveDialog(
                                                SettingsDialog.EventView
                                            )
                                        )
                                    }
                                )
                            }, {
                                AppCell(
                                    start = {
                                        AnimatedIcon(
                                            targetState = state.appSettings.eventsCountView,
                                            iconProvider = { state ->
                                                when (state) {
                                                    EventsCountView.DETAILS -> {
                                                        AppTheme.icons.points
                                                    }

                                                    EventsCountView.BRIEFLY -> {
                                                        AppTheme.icons.one
                                                    }

                                                    EventsCountView.OFF -> {
                                                        AppTheme.icons.visibilityOff
                                                    }
                                                }
                                            }
                                        )
                                    },
                                    center = {
                                        CenterContent(
                                            title = {
                                                TextBlock(
                                                    text = stringResource(R.string.settings_show_count_classes)
                                                )
                                            },
                                            subtitle = {
                                                TextBlock(
                                                    text = when (state.appSettings.eventsCountView) {
                                                        EventsCountView.DETAILS -> stringResource(R.string.settings_details)
                                                        EventsCountView.BRIEFLY -> stringResource(R.string.settings_briefly)
                                                        EventsCountView.OFF -> stringResource(R.string.settings_off)
                                                    },
                                                    typography = AppTheme.typography.bodyMedium,
                                                    color = AppTheme.colors.onSecondaryContainer,
                                                )
                                            }
                                        )
                                    },
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetActiveDialog(
                                                SettingsDialog.CountEvents
                                            )
                                        )
                                    }
                                )
                            }, {
                                AppCell(
                                    start = {
                                        Icon(
                                            icon = AppTheme.icons.sun,
                                            iconSize = 20.dp,
                                            iconColor = AppTheme.colors.onSecondaryContainer
                                        )
                                    },
                                    center = {
                                        TextBlock(
                                            text = stringResource(R.string.settings_use_image),
                                        )
                                    },
                                    end = {
                                        Custom {
                                            AppSwitch(
                                                checked = state.appSettings.usedImageInReview
                                            ) {
                                                onAction(
                                                    SettingsAction.OnSetUsedImageInReview(it)
                                                )
                                            }
                                        }
                                    },
                                    showChevron = false,
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetUsedImageInReview(
                                                !state.appSettings.usedImageInReview
                                            )
                                        )
                                    }
                                )
                            }
                        )
                    )
                }

                item {
                    ColumnGroup(
                        title = stringResource(R.string.settings_schedule),
                        items = listOf(
                            {
                                AppCell(
                                    start = {
                                        AnimatedIcon(
                                            targetState = state.appSettings.eventExtraPolicy,
                                            iconProvider = { state ->
                                                when (state) {
                                                    EventExtraPolicy.SYNCHRONIZED -> {
                                                        AppTheme.icons.sync
                                                    }

                                                    EventExtraPolicy.BY_DATES -> {
                                                        AppTheme.icons.calendar
                                                    }

                                                    EventExtraPolicy.DEFAULT -> {
                                                        AppTheme.icons.split
                                                    }
                                                }
                                            }
                                        )
                                    },
                                    center = {
                                        CenterContent(
                                            title = {
                                                TextBlock(
                                                    text = stringResource(R.string.settings_comments_and_tags)
                                                )
                                            },
                                            subtitle = {
                                                TextBlock(
                                                    text = when (state.appSettings.eventExtraPolicy) {
                                                        EventExtraPolicy.DEFAULT -> stringResource(R.string.settings_by_default)
                                                        EventExtraPolicy.SYNCHRONIZED -> stringResource(
                                                            R.string.settings_synchronized
                                                        )

                                                        EventExtraPolicy.BY_DATES -> stringResource(R.string.settings_by_dates)
                                                    },
                                                    typography = AppTheme.typography.bodyMedium,
                                                    color = AppTheme.colors.onSecondaryContainer,
                                                )
                                            }
                                        )
                                    },
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetActiveDialog(
                                                SettingsDialog.EventExtraPolicy
                                            )
                                        )
                                    }
                                )
                            }, {
                                AppCell(
                                    start = {
                                        Icon(
                                            icon = AppTheme.icons.undelete,
                                            iconSize = 20.dp,
                                            iconColor = AppTheme.colors.onSecondaryContainer
                                        )
                                    },
                                    center = {
                                        CenterContent(
                                            title = {
                                                TextBlock(
                                                    text = stringResource(R.string.settings_not_delete_schedules),
                                                )
                                            },
                                            subtitle = {
                                                TextBlock(
                                                    text = stringResource(R.string.settings_not_delete_schedules_message),
                                                    maxLines = 3,
                                                    typography = AppTheme.typography.bodyMedium,
                                                    color = AppTheme.colors.onSecondaryContainer,
                                                )
                                            },
                                        )
                                    },
                                    end = {
                                        Custom {
                                            AppSwitch(
                                                checked = !state.appSettings.schedulesDeletable
                                            ) {
                                                onAction(SettingsAction.OnSetSchedulesDeletable(!it))
                                            }
                                        }
                                    },
                                    showChevron = false,
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetSchedulesDeletable(!state.appSettings.schedulesDeletable)
                                        )
                                    }
                                )
                            }
                        )

                    )
                }

                item {
                    ColumnGroup(
                        title = stringResource(R.string.settings_general),
                        items = listOf(
                            {
                                AppCell(
                                    start = {
                                        AnimatedIcon(
                                            targetState = state.appSettings.themePreferences.theme.isDarkTheme(),
                                            iconProvider = { state ->
                                                if (state) {
                                                    AppTheme.icons.moon
                                                } else {
                                                    AppTheme.icons.sun
                                                }
                                            }
                                        )
                                    },
                                    center = {
                                        CenterContent(
                                            title = {
                                                TextBlock(
                                                    text = stringResource(R.string.settings_theme)
                                                )
                                            },
                                            subtitle = {
                                                TextBlock(
                                                    text = when (state.appSettings.themePreferences.theme) {
                                                        Theme.LIGHT -> stringResource(R.string.settings_light)
                                                        Theme.DARK -> stringResource(R.string.settings_dark)
                                                        Theme.SYSTEM -> stringResource(R.string.settings_auto)
                                                    },
                                                    typography = AppTheme.typography.bodyMedium,
                                                    color = AppTheme.colors.onSecondaryContainer,
                                                )
                                            },
                                        )
                                    },
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetActiveDialog(
                                                SettingsDialog.Decor
                                            )
                                        )
                                    }
                                )
                            }, {
                                AppCell(
                                    start = {
                                        Icon(
                                            icon = AppTheme.icons.info,
                                            iconSize = 20.dp,
                                            iconColor = AppTheme.colors.onSecondaryContainer,
                                            showBadge = state.updatesAvailable,
                                        )
                                    },
                                    center = {
                                        TextBlock(
                                            modifier = Modifier.padding(
                                                vertical = AppTheme.paddings.padding8
                                            ),
                                            text = stringResource(R.string.settings_about_app)
                                        )
                                    },
                                    onClick = {
                                        onAction(
                                            SettingsAction.OnSetActiveDialog(
                                                SettingsDialog.Info
                                            )
                                        )
                                    }
                                )
                            }
                        )
                    )
                }
            }
            StatusBarProtection()

            when (state.activeDialog) {
                is SettingsDialog.ScheduleView -> {
                    ScheduleViewModalDialog(
                        scheduleView = state.appSettings.scheduleView,
                        onAction = onAction,
                        onDismiss = {
                            onAction(
                                SettingsAction.OnSetActiveDialog(null)
                            )
                        }
                    )
                }

                is SettingsDialog.EventView -> {
                    EventViewModalDialog(
                        eventView = state.appSettings.eventView,
                        onAction = onAction,
                        onDismiss = {
                            onAction(
                                SettingsAction.OnSetActiveDialog(null)
                            )
                        }
                    )
                }

                is SettingsDialog.Info -> {
                    InfoModalDialog(
                        updatesAvailable = state.updatesAvailable,
                        onDismiss = {
                            onAction(
                                SettingsAction.OnSetActiveDialog(null)
                            )
                        }
                    )
                }

                is SettingsDialog.Decor -> {
                    ThemeModalDialog(
                        themePreferences = state.appSettings.themePreferences,
                        onAction = onAction,
                        onDismiss = {
                            onAction(
                                SettingsAction.OnSetActiveDialog(null)
                            )
                        }
                    )
                }

                is SettingsDialog.CountEvents -> {
                    CountEventsModalDialog(
                        eventsCountView = state.appSettings.eventsCountView,
                        onAction = onAction,
                        onDismiss = {
                            onAction(
                                SettingsAction.OnSetActiveDialog(null)
                            )
                        }
                    )
                }

                is SettingsDialog.EventExtraPolicy -> {
                    EventExtraPolicyModalDialog(
                        eventExtraPolicy = state.appSettings.eventExtraPolicy,
                        onAction = onAction,
                        onDismiss = {
                            onAction(
                                SettingsAction.OnSetActiveDialog(null)
                            )
                        }
                    )
                }

                else -> {}
            }
        }
    }

}