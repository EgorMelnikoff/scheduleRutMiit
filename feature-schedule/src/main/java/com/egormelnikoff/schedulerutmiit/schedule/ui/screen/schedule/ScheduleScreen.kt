package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScreenState
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.AnimatedAlert
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomPullToRefreshBox
import com.egormelnikoff.schedulerutmiit.core.ui.elements.GridGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.ScheduleLoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.element.ModalDialogSchedule
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.element.ScheduleTopAppBar
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.state.NamedScheduleState
import java.time.LocalDate

@Composable
fun ScreenSchedule(
    scheduleCalendarState: CalendarState?,
    scheduleListState: LazyListState,

    namedScheduleState: NamedScheduleState,
    screenState: ScreenState,
    appSettings: AppSettings,

    contentPadding: PaddingValues,

    scheduleViewModel: ScheduleViewModel,
    launch: (Array<String>) -> Unit,
    isToday: (LocalDate) -> Boolean,
    onOpenDialog: (Route.Dialog) -> Unit,
    onSetScheduleView: (ScheduleView) -> Unit,
) {
    var backDialog by remember { mutableStateOf(false) }
    var namedScheduleDialog by remember { mutableStateOf<Boolean?>(null) }
    var deleteNamedScheduleDialog by remember { mutableStateOf(false) }

    when {
        namedScheduleState is NamedScheduleState.Loading -> ScheduleLoadingScreen()

        namedScheduleState is NamedScheduleState.Empty -> {
            ErrorScreen(
                title = stringResource(R.string.no_saved_schedule),
                subtitle = stringResource(R.string.empty_base),
                button = {
                    GridGroup(
                        items = listOf(
                            listOf(
                                { shape ->
                                    CustomButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        buttonTitle = stringResource(R.string.find),
                                        imageVector = ImageVector.vectorResource(R.drawable.search),
                                        shape = shape,
                                        onClick = { onOpenDialog(Route.Dialog.SearchDialog) },
                                    )
                                },
                                { shape ->
                                    CustomButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        buttonTitle = stringResource(R.string.create),
                                        imageVector = ImageVector.vectorResource(R.drawable.add),
                                        shape = shape,
                                        onClick = { onOpenDialog(Route.Dialog.AddScheduleDialog) },
                                    )
                                }
                            ),
                            listOf { shape ->
                                CustomButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    buttonTitle = stringResource(R.string._import),
                                    imageVector = ImageVector.vectorResource(R.drawable.resource_import),
                                    shape = shape,
                                    onClick = { launch(arrayOf("application/json")) },
                                )
                            }
                        )
                    )
                },
                paddingBottom = contentPadding.calculateBottomPadding() - 16.dp
            )
        }

        screenState.isLoading -> {
            BackHandler {
                scheduleViewModel.cancelLoading()
            }
            ScheduleLoadingScreen()
        }

        screenState.isError -> {
            ErrorScreen(
                title = stringResource(R.string.error),
                subtitle = stringResource(R.string.error_load_schedule),
                paddingTop = contentPadding.calculateTopPadding(),
                button = {
                    CustomButton(
                        modifier = Modifier.fillMaxWidth(),
                        buttonTitle = stringResource(R.string.return_default),
                        imageVector = ImageVector.vectorResource(R.drawable.back),
                        onClick = { scheduleViewModel.refreshScheduleState(showLoading = false) },
                    )
                },
                paddingBottom = contentPadding.calculateBottomPadding()
            )
        }

        namedScheduleState is NamedScheduleState.Loaded -> {
            BackHandler(
                !namedScheduleState.namedScheduleWithSchedules.namedSchedule.isDefault
            ) {
                backDialog = true
            }

            BackHandler(
                screenState.isRefreshing
            ) {
                scheduleViewModel.cancelRefresh()
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    ScheduleTopAppBar(
                        onSetScheduleView = onSetScheduleView,
                        onShowNamedScheduleDialog = { newValue ->
                            namedScheduleDialog = newValue
                        },
                        namedSchedule = namedScheduleState.namedScheduleWithSchedules.namedSchedule,
                        scheduleTypeName = namedScheduleState.scheduleState?.schedule?.timetableType?.typeName,
                        scheduleView = appSettings.scheduleView
                    )
                }
            ) { padding ->
                if (scheduleCalendarState != null && namedScheduleState.scheduleState?.schedule != null) {
                    CustomPullToRefreshBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = padding.calculateTopPadding()),
                        onRefresh = {
                            scheduleViewModel.refreshScheduleState(
                                showLoading = false,
                                updating = true,
                                namedScheduleId = namedScheduleState.namedScheduleWithSchedules.namedSchedule.id
                            )
                        },
                        enabled = screenState.isSaved,
                        isRefreshing = screenState.isRefreshing
                    ) {
                        Column {
                            AnimatedAlert(
                                isHidden = screenState.isSaved
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(
                                            MaterialTheme.colorScheme.primary
                                        )
                                ) {
                                    ClickableItem(
                                        verticalPadding = 4.dp,
                                        title = stringResource(R.string.schedule_is_not_saved),
                                        titleColor = MaterialTheme.colorScheme.onPrimary,
                                        titleTypography = MaterialTheme.typography.titleSmall,
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier.size(24.dp),
                                                imageVector = ImageVector.vectorResource(R.drawable.info),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        },
                                        trailingIcon = {
                                            Button(
                                                colors = ButtonDefaults.buttonColors()
                                                    .copy(
                                                        containerColor = MaterialTheme.colorScheme.primary,
                                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                                    ),
                                                onClick = {
                                                    scheduleViewModel.saveCurrentNamedSchedule()
                                                },
                                                contentPadding = PaddingValues(
                                                    horizontal = 8.dp,
                                                    vertical = 2.dp
                                                )
                                            ) {
                                                CompositionLocalProvider(
                                                    LocalMinimumInteractiveComponentSize provides 0.dp
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.save),
                                                        style = MaterialTheme.typography.titleSmall
                                                    )
                                                }
                                            }

                                        }
                                    )
                                }
                            }
                            AnimatedContent(
                                targetState = appSettings.scheduleView,
                                transitionSpec = {
                                    fadeIn() + slideInVertically(
                                        initialOffsetY = { it / 2 }
                                    ) togetherWith fadeOut() +
                                            slideOutVertically(
                                                targetOffsetY = { it / 2 }
                                            )
                                }
                            ) { targetState ->
                                when (targetState) {
                                    ScheduleView.CALENDAR -> {
                                        ScheduleCalendar(
                                            scheduleState = namedScheduleState.scheduleState,
                                            isSavedSchedule = screenState.isSaved,

                                            scheduleCalendarState = scheduleCalendarState,
                                            appSettings = appSettings,
                                            paddingBottom = contentPadding.calculateBottomPadding(),
                                            onOpenDialog = onOpenDialog,
                                            isToday = isToday
                                        ) { eventAction ->
                                            scheduleViewModel.eventAction(eventAction)
                                        }
                                    }

                                    ScheduleView.LIST -> {
                                        ScheduleList(
                                            scheduleListState = scheduleListState,

                                            isSavedSchedule = screenState.isSaved,
                                            scheduleState = namedScheduleState.scheduleState,

                                            appSettings = appSettings,
                                            paddingBottom = contentPadding.calculateBottomPadding(),
                                            onOpenDialog = onOpenDialog
                                        ) { eventAction ->
                                            scheduleViewModel.eventAction(eventAction)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (deleteNamedScheduleDialog) {
                        CustomAlertDialog(
                            dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                            dialogTitle = "${stringResource(R.string.delete_schedule)}?",
                            dialogText = stringResource(R.string.impossible_restore_eventextra),
                            onDismissRequest = {
                                deleteNamedScheduleDialog = false
                            },
                            onConfirmation = {
                                scheduleViewModel.deleteNamedSchedule(
                                    namedScheduleState.namedScheduleWithSchedules.namedSchedule.id,
                                    namedScheduleState.namedScheduleWithSchedules.namedSchedule.isDefault
                                )
                            }
                        )
                    }
                } else {
                    Empty(
                        title = "¯\\_(ツ)_/¯",
                        subtitle = stringResource(R.string.empty_here),
                        isBoldTitle = false,
                        paddingBottom = contentPadding.calculateBottomPadding()
                    )
                }
            }

            namedScheduleDialog?.let {
                ModalDialogSchedule(
                    namedSchedule = namedScheduleState.namedScheduleWithSchedules.namedSchedule,
                    currentSchedule = namedScheduleState.scheduleState?.schedule,
                    schedulesWithEvents = namedScheduleState.namedScheduleWithSchedules.schedulesWithEvents,
                    scheduleViewModel = scheduleViewModel,

                    isSavedNamedSchedule = screenState.isSaved,
                    isDefaultNamedSchedule = it,
                    haveHiddenEvents = namedScheduleState.scheduleState?.haveHiddenEvents ?: false,
                    haveNotEmptySchedules = namedScheduleState.namedScheduleWithSchedules.schedulesWithEvents.isNotEmpty() && namedScheduleState.scheduleState?.schedule != null,
                    onOpenDialog = onOpenDialog,
                    onDeleteNamedSchedule = { namedScheduleId, isDefault ->
                        scheduleViewModel.deleteNamedSchedule(namedScheduleId, isDefault)
                    }
                ) {
                    namedScheduleDialog = null
                }
            }
        }


        else -> {
            ErrorScreen(
                title = stringResource(R.string.error),
                paddingBottom = contentPadding.calculateBottomPadding()
            )
        }
    }

    if (backDialog) {
        CustomAlertDialog(
            dialogTitle = stringResource(R.string.return_default_schedule),
            dialogText = stringResource(R.string.do_you_want_continue),
            onDismissRequest = {
                backDialog = false
            },
            onConfirmation = {
                scheduleViewModel.refreshScheduleState(showLoading = false)
            }
        )
    }
}