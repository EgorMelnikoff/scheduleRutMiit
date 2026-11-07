package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog.AppAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.rememberCalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.pull_to_refresh.AppPullToRefreshBox
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.core.ui.containers.AnimatedAlert
import com.egormelnikoff.schedulerutmiit.core.ui.containers.GridGroup
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ErrorScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget.ScheduleCalendarWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget.ScheduleHeaderWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget.ScheduleListWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget.ScheduleLoadingWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget.ScheduleSheetWidget

@Composable
fun ScreenSchedule(
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
) {
    val scheduleViewModel = hiltViewModel<ScheduleViewModel>()
    val namedScheduleState =
        scheduleViewModel.namedScheduleState.collectAsStateWithLifecycle().value
    val hourlyDateTime by scheduleViewModel.hourlyDateTime.collectAsStateWithLifecycle()
    val appSettings by scheduleViewModel.appSettings.collectAsStateWithLifecycle()
    val screenState by scheduleViewModel.screenState.collectAsStateWithLifecycle()

    val scheduleCalendarState = rememberScheduleCalendarState(namedScheduleState)
    val scheduleListState = rememberLazyListState()


    UiEventProcessor(
        scheduleViewModel.uiEvent,
        snackbarHostState
    )

    var backDialog by remember { mutableStateOf(false) }
    var namedScheduleDialog by remember { mutableStateOf<Boolean?>(null) }
    var deleteNamedScheduleDialog by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        scheduleViewModel.onImportData(uri)
    }




    when {
        namedScheduleState is NamedScheduleState.Loading -> ScheduleLoadingWidget()

        namedScheduleState is NamedScheduleState.Empty -> {
            ErrorScreen(
                title = stringResource(R.string.no_saved_schedule),
                subtitle = stringResource(R.string.empty_base),
                button = {
                    GridGroup(
                        items = listOf(
                            listOf(
                                { shape ->
                                    AppButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        buttonTitle = stringResource(R.string.find),
                                        imageVector = ImageVector.vectorResource(R.drawable.search),
                                        shape = shape,
                                        onClick = {
                                            scheduleViewModel.onOpenDialog(Route.Dialog.SearchDialog)
                                        },
                                    )
                                },
                                { shape ->
                                    AppButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        buttonTitle = stringResource(R.string.create),
                                        imageVector = ImageVector.vectorResource(R.drawable.add),
                                        shape = shape,
                                        onClick = {  scheduleViewModel.onOpenDialog(Route.Dialog.AddScheduleDialog) },
                                    )
                                }
                            ),
                            listOf { shape ->
                                AppButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    buttonTitle = stringResource(R.string._import),
                                    imageVector = ImageVector.vectorResource(R.drawable.resource_import),
                                    shape = shape,
                                    onClick = { importLauncher.launch(arrayOf("application/json")) },
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
            ScheduleLoadingWidget()
        }

        screenState.isError -> {
            ErrorScreen(
                title = stringResource(R.string.error),
                subtitle = stringResource(R.string.error_load_schedule),
                paddingTop = contentPadding.calculateTopPadding(),
                button = {
                    AppButton(
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
                    ScheduleHeaderWidget(
                        onSetScheduleView = {
                            scheduleViewModel.setScheduleView(it)
                        },
                        onShowNamedScheduleDialog = { newValue ->
                            namedScheduleDialog = newValue
                        },
                        namedSchedule = namedScheduleState.namedScheduleWithSchedules.namedSchedule,
                        scheduleTypeName = namedScheduleState.scheduleState?.schedule?.timetableType?.typeName,
                        scheduleView = appSettings?.scheduleView ?: ScheduleView.CALENDAR
                    )
                }
            ) { padding ->
                if (scheduleCalendarState != null && namedScheduleState.scheduleState?.schedule != null) {
                    AppPullToRefreshBox(
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
                                    AppCell(
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
                                targetState = appSettings?.scheduleView,
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
                                    ScheduleView.LIST -> {
                                        ScheduleListWidget(
                                            scheduleListState = scheduleListState,

                                            isSavedSchedule = screenState.isSaved,
                                            scheduleState = namedScheduleState.scheduleState,

                                            appSettings = appSettings!!, //TODO
                                            paddingBottom = contentPadding.calculateBottomPadding(),
                                            onOpenDialog = { dialog ->
                                                scheduleViewModel.onOpenDialog(dialog)
                                            },
                                            onDeleteEvent = { eventId ->
                                                scheduleViewModel.onDeleteEvent(eventId)
                                            },
                                            onHideEvent = { eventId ->
                                                scheduleViewModel.onHideEvent(eventId)
                                            }
                                        )
                                    }

                                    else -> {
                                        ScheduleCalendarWidget(
                                            scheduleState = namedScheduleState.scheduleState,
                                            isSavedSchedule = screenState.isSaved,

                                            scheduleCalendarState = scheduleCalendarState,
                                            appSettings = appSettings!!,
                                            paddingBottom = contentPadding.calculateBottomPadding(),
                                            onOpenDialog = { dialog ->
                                                scheduleViewModel.onOpenDialog(dialog)
                                            },
                                            isToday = { currentDate ->
                                                hourlyDateTime.toLocalDate() == currentDate
                                            },
                                            onDeleteEvent = { eventId ->
                                                scheduleViewModel.onDeleteEvent(eventId)
                                            },
                                            onHideEvent = { eventId ->
                                                scheduleViewModel.onHideEvent(eventId)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (deleteNamedScheduleDialog) {
                        AppAlertDialog(
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
                    EmptyScreen(
                        title = "¯\\_(ツ)_/¯",
                        subtitle = stringResource(R.string.empty_here),
                        isBoldTitle = false,
                        paddingBottom = contentPadding.calculateBottomPadding()
                    )
                }
            }

            namedScheduleDialog?.let {
                ScheduleSheetWidget(
                    namedSchedule = namedScheduleState.namedScheduleWithSchedules.namedSchedule,
                    currentSchedule = namedScheduleState.scheduleState?.schedule,
                    schedules = namedScheduleState.namedScheduleWithSchedules.schedulesWithEvents.map { s -> s.schedule },
                    scheduleViewModel = scheduleViewModel,

                    isSavedNamedSchedule = screenState.isSaved,
                    haveHiddenEvents = namedScheduleState.scheduleState?.haveHiddenEvents ?: false,
                    haveNotEmptySchedules = namedScheduleState.namedScheduleWithSchedules.schedulesWithEvents.isNotEmpty() && namedScheduleState.scheduleState?.schedule != null,
                    onOpenDialog = { dialog ->
                        scheduleViewModel.onOpenDialog(dialog)
                    },
                    onDeleteNamedSchedule = {
                        scheduleViewModel.deleteNamedSchedule(
                            namedScheduleState.namedScheduleWithSchedules.namedSchedule.id,
                            namedScheduleState.namedScheduleWithSchedules.namedSchedule.isDefault
                        )
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
        AppAlertDialog(
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

@Composable
fun rememberScheduleCalendarState(
    namedScheduleState: NamedScheduleState
): CalendarState? {
    val calendarData = (namedScheduleState as? NamedScheduleState.Loaded)
        ?.scheduleState
        ?.calendarData

    return if (calendarData != null) {
        rememberCalendarState(calendarData)
    } else {
        null
    }
}