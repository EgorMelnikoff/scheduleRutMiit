package com.egormelnikoff.schedulerutmiit.core.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
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
    contentPadding: PaddingValues,
) {
    if (state.appSettings == null) {
        LoadingScreen()
    } else {
        val settingsListState = rememberLazyStaggeredGridState()
        val visibleSettingsIds by remember(state.appSettings.eventView) {
            derivedStateOf {
                buildList {
                    if (state.appSettings.eventView.groupsVisible) add(R.string.groups)
                    if (state.appSettings.eventView.roomsVisible) add(R.string.rooms)
                    if (state.appSettings.eventView.lecturersVisible) add(R.string.lecturers)
                    if (state.appSettings.eventView.tagVisible) add(R.string.tag)
                    if (state.appSettings.eventView.commentVisible) add(R.string.comment)
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
                    title = stringResource(R.string._interface),
                    items = listOf(
                        {
                            AppCell(
                                start = {
                                    AnimatedIcon(
                                        targetState = state.appSettings.scheduleView,
                                        iconProvider = { state ->
                                            when (state) {
                                                ScheduleView.CALENDAR -> {
                                                    ImageVector.vectorResource(R.drawable.calendar)
                                                }

                                                ScheduleView.LIST -> {
                                                    ImageVector.vectorResource(R.drawable.list)
                                                }
                                            }
                                        }
                                    )
                                },
                                center = {
                                    CenterContent(
                                        title = {
                                            TextBlock(
                                                text = stringResource(R.string.schedule_view),
                                            )
                                        },
                                        subtitle = {
                                            TextBlock(
                                                text = when (state.appSettings.scheduleView) {
                                                    ScheduleView.CALENDAR -> stringResource(R.string.calendar)
                                                    ScheduleView.LIST -> stringResource(R.string.full_list)
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
                                        icon = ImageVector.vectorResource(R.drawable.event_view),
                                        iconSize = 20.dp,
                                        iconColor = AppTheme.colors.onSecondaryContainer
                                    )
                                },
                                center = {
                                    CenterContent(
                                        title = {
                                            TextBlock(
                                                text = stringResource(R.string.event_view)
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
                                                    ImageVector.vectorResource(
                                                        R.drawable.points
                                                    )
                                                }

                                                EventsCountView.BRIEFLY -> {
                                                    ImageVector.vectorResource(R.drawable.one)
                                                }

                                                EventsCountView.OFF -> {
                                                    ImageVector.vectorResource(R.drawable.visibility_off)
                                                }
                                            }
                                        }
                                    )
                                },
                                center = {
                                    CenterContent(
                                        title = {
                                            TextBlock(
                                                text = stringResource(R.string.show_count_classes)
                                            )
                                        },
                                        subtitle = {
                                            TextBlock(
                                                text = when (state.appSettings.eventsCountView) {
                                                    EventsCountView.DETAILS -> stringResource(R.string.details)
                                                    EventsCountView.BRIEFLY -> stringResource(R.string.briefly)
                                                    EventsCountView.OFF -> stringResource(R.string.off)
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
                                        icon = ImageVector.vectorResource(R.drawable.sun),
                                        iconSize = 20.dp,
                                        iconColor = AppTheme.colors.onSecondaryContainer
                                    )
                                },
                                center = {
                                    TextBlock(
                                        text = stringResource(R.string.use_image),
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
                    title = stringResource(R.string.schedule),
                    items = listOf(
                        {
                            AppCell(
                                start = {
                                    AnimatedIcon(
                                        targetState = state.appSettings.eventExtraPolicy,
                                        iconProvider = { state ->
                                            when (state) {
                                                EventExtraPolicy.SYNCHRONIZED -> {
                                                    ImageVector.vectorResource(R.drawable.sync)
                                                }

                                                EventExtraPolicy.BY_DATES -> {
                                                    ImageVector.vectorResource(R.drawable.calendar)
                                                }

                                                EventExtraPolicy.DEFAULT -> {
                                                    ImageVector.vectorResource(R.drawable.split)
                                                }
                                            }
                                        }
                                    )
                                },
                                center = {
                                    CenterContent(
                                        title = {
                                            TextBlock(
                                                text = stringResource(R.string.comments_and_tags)
                                            )
                                        },
                                        subtitle = {
                                            TextBlock(
                                                text = when (state.appSettings.eventExtraPolicy) {
                                                    EventExtraPolicy.DEFAULT -> stringResource(R.string.by_default)
                                                    EventExtraPolicy.SYNCHRONIZED -> stringResource(
                                                        R.string._synchronized
                                                    )

                                                    EventExtraPolicy.BY_DATES -> stringResource(R.string.by_dates)
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
                                        icon = ImageVector.vectorResource(R.drawable.undelete),
                                        iconSize = 20.dp,
                                        iconColor = AppTheme.colors.onSecondaryContainer
                                    )
                                },
                                center = {
                                    CenterContent(
                                        title = {
                                            TextBlock(
                                                text = stringResource(R.string.not_delete_schedules),
                                            )
                                        },
                                        subtitle = {
                                            TextBlock(
                                                text = stringResource(R.string.not_delete_schedules_message),
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
                    title = stringResource(R.string.general),
                    items = listOf(
                        {
                            AppCell(
                                start = {
                                    AnimatedIcon(
                                        targetState = state.appSettings.themePreferences.theme.isDarkTheme(),
                                        iconProvider = { state ->
                                            if (state) {
                                                ImageVector.vectorResource(R.drawable.moon)
                                            } else {
                                                ImageVector.vectorResource(R.drawable.sun)
                                            }
                                        }
                                    )
                                },
                                center = {
                                    CenterContent(
                                        title = {
                                            TextBlock(
                                                text = stringResource(R.string.theme)
                                            )
                                        },
                                        subtitle = {
                                            TextBlock(
                                                text = when (state.appSettings.themePreferences.theme) {
                                                    Theme.LIGHT -> stringResource(R.string.light)
                                                    Theme.DARK -> stringResource(R.string.dark)
                                                    Theme.SYSTEM -> stringResource(R.string.auto)
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
                                        icon = ImageVector.vectorResource(R.drawable.info),
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
                                        text = stringResource(R.string.about_app)
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