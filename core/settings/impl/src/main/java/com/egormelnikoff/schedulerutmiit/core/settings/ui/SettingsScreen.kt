package com.egormelnikoff.schedulerutmiit.core.settings.ui

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
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
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
                .background(MaterialTheme.colorScheme.background),
            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
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
                                title = stringResource(R.string.schedule_view),
                                leadingIcon = {
                                    AnimatedContent(
                                        targetState = state.appSettings.scheduleView,
                                        transitionSpec = {
                                            scaleIn() togetherWith scaleOut()
                                        }
                                    ) { state ->
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            imageVector = when (state) {
                                                ScheduleView.CALENDAR -> ImageVector.vectorResource(
                                                    R.drawable.calendar
                                                )

                                                ScheduleView.LIST -> ImageVector.vectorResource(R.drawable.list)
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                },
                                subtitle = when (state.appSettings.scheduleView) {
                                    ScheduleView.CALENDAR -> stringResource(R.string.calendar)
                                    ScheduleView.LIST -> stringResource(R.string.full_list)
                                },
                                defaultMinHeight = 36.dp,
                                enableToolTip = true
                            ) {
                                onAction(
                                    SettingsAction.OnSetActiveDialog(
                                        SettingsDialog.ScheduleView
                                    )
                                )
                            }
                        }, {
                            AppCell(
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
                                defaultMinHeight = 36.dp,
                                enableToolTip = true
                            ) {
                                onAction(
                                    SettingsAction.OnSetActiveDialog(
                                        SettingsDialog.EventView
                                    )
                                )
                            }
                        }, {
                            AppCell(
                                title = stringResource(R.string.show_count_classes),
                                subtitle = when (state.appSettings.eventsCountView) {
                                    EventsCountView.DETAILS -> stringResource(R.string.details)
                                    EventsCountView.BRIEFLY -> stringResource(R.string.briefly)
                                    EventsCountView.OFF -> stringResource(R.string.off)
                                },
                                leadingIcon = {
                                    AnimatedContent(
                                        targetState = state.appSettings.eventsCountView,
                                        transitionSpec = {
                                            scaleIn() togetherWith scaleOut()
                                        }
                                    ) { state ->
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            imageVector = when (state) {
                                                EventsCountView.DETAILS -> ImageVector.vectorResource(
                                                    R.drawable.points
                                                )

                                                EventsCountView.BRIEFLY -> ImageVector.vectorResource(
                                                    R.drawable.one
                                                )

                                                EventsCountView.OFF -> ImageVector.vectorResource(R.drawable.visibility_off)
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                },
                                defaultMinHeight = 36.dp,
                                enableToolTip = true
                            ) {
                                onAction(
                                    SettingsAction.OnSetActiveDialog(
                                        SettingsDialog.CountEvents
                                    )
                                )
                            }
                        }, {
                            AppCell(
                                title = stringResource(R.string.use_image),
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.sun),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                },
                                defaultMinHeight = 36.dp,
                                trailingIcon = {
                                    AppSwitch(
                                        checked = state.appSettings.usedImageInReview
                                    ) {
                                        onAction(
                                            SettingsAction.OnSetUsedImageInReview(it)
                                        )
                                    }
                                },
                                showClickLabel = false,
                                enableToolTip = true
                            ) {
                                onAction(
                                    SettingsAction.OnSetUsedImageInReview(
                                        !state.appSettings.usedImageInReview
                                    )
                                )
                            }
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
                                title = stringResource(R.string.comments_and_tags),
                                subtitle = when (state.appSettings.eventExtraPolicy) {
                                    EventExtraPolicy.DEFAULT -> stringResource(R.string.by_default)
                                    EventExtraPolicy.SYNCHRONIZED -> stringResource(R.string._synchronized)
                                    EventExtraPolicy.BY_DATES -> stringResource(R.string.by_dates)
                                },
                                leadingIcon = {
                                    AnimatedContent(
                                        targetState = state.appSettings.eventExtraPolicy,
                                        transitionSpec = {
                                            scaleIn() togetherWith scaleOut()
                                        }
                                    ) { state ->
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            imageVector = when (state) {
                                                EventExtraPolicy.SYNCHRONIZED -> ImageVector.vectorResource(
                                                    R.drawable.sync
                                                )

                                                EventExtraPolicy.BY_DATES -> ImageVector.vectorResource(
                                                    R.drawable.calendar
                                                )

                                                EventExtraPolicy.DEFAULT -> ImageVector.vectorResource(
                                                    R.drawable.split
                                                )
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                },
                                defaultMinHeight = 36.dp,
                                enableToolTip = true
                            ) {
                                onAction(
                                    SettingsAction.OnSetActiveDialog(
                                        SettingsDialog.EventExtraPolicy
                                    )
                                )
                            }
                        }, {
                            AppCell(
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
                                    AppSwitch(
                                        checked = !state.appSettings.schedulesDeletable
                                    ) {
                                        onAction(SettingsAction.OnSetSchedulesDeletable(!it))
                                    }
                                },
                                showClickLabel = false,
                                enableToolTip = true
                            ) {
                                onAction(SettingsAction.OnSetSchedulesDeletable(!state.appSettings.schedulesDeletable))
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
                            AppCell(
                                title = stringResource(R.string.theme),
                                subtitle = when (state.appSettings.themePreferences.theme) {
                                    Theme.LIGHT -> stringResource(R.string.light)
                                    Theme.DARK -> stringResource(R.string.dark)
                                    Theme.SYSTEM -> stringResource(R.string.auto)
                                },
                                leadingIcon = {
                                    AnimatedContent(
                                        targetState = state.appSettings.themePreferences.theme.isDarkTheme(),
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
                                defaultMinHeight = 36.dp,
                                enableToolTip = true
                            ) {
                                onAction(
                                    SettingsAction.OnSetActiveDialog(
                                        SettingsDialog.Decor
                                    )
                                )
                            }
                        }, {
                            AppCell(
                                title = stringResource(R.string.about_app),
                                showBadge = state.updatesAvailable,
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.info),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                },
                                defaultMinHeight = 36.dp,
                                enableToolTip = true
                            ) {
                                onAction(
                                    SettingsAction.OnSetActiveDialog(
                                        SettingsDialog.Info
                                    )
                                )
                            }
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