package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.AnimatedAlert
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomPullToRefreshBox
import com.egormelnikoff.schedulerutmiit.ui.elements.ModalDialogNamedSchedule
import com.egormelnikoff.schedulerutmiit.ui.elements.ScheduleTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.ScheduleLoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calendar.ScheduleCalendarView
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.list.ScheduleListView
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.split_weeks.ScheduleSplitWeeksView
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel
import java.time.LocalDateTime

@Composable
fun ScreenSchedule(
    appUiState: AppUiState,
    scheduleState: ScheduleState,
    scheduleUiState: ScheduleUiState?,
    appSettings: AppSettings,
    currentDateTime: LocalDateTime,
    scheduleViewModel: ScheduleViewModel,
    settingsViewModel: SettingsViewModel,
    externalPadding: PaddingValues
) {
    var showBackDialog by remember { mutableStateOf(false) }
    var showNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf(false) }

    when {
        scheduleState.isLoading -> {
            BackHandler {
                scheduleViewModel.cancelLoading()
            }
            ScheduleLoadingScreen()
        }

        scheduleState.isError -> {
            ErrorScreen(
                title = stringResource(R.string.error),
                subtitle = stringResource(R.string.error_load_schedule),
                paddingTop = externalPadding.calculateTopPadding(),
                button = {
                    CustomButton(
                        modifier = Modifier.fillMaxWidth(),
                        buttonTitle = stringResource(R.string.return_default),
                        imageVector = ImageVector.vectorResource(R.drawable.back),
                        onClick = { scheduleViewModel.refreshScheduleState(showLoading = false) },
                    )
                },
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        scheduleState.currentNamedScheduleData?.namedSchedule != null -> {
            BackHandler(
                scheduleState.savedNamedSchedules.isNotEmpty() && !scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.isDefault
            ) {
                showBackDialog = true
            }

            BackHandler(
                scheduleState.isRefreshing
            ) {
                scheduleViewModel.cancelRefresh()
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    ScheduleTopAppBar(
                        navigateToHiddenEvents = { namedScheduleEntity ->
                            appUiState.appBackStack.openDialog(
                                Route.Dialog.HiddenEventsDialog(
                                    namedScheduleEntity
                                )
                            )
                        },
                        onSetScheduleView = { value ->
                            settingsViewModel.onSetScheduleView(value)
                        },
                        onShowNamedScheduleDialog = { newValue ->
                            showNamedScheduleDialog = newValue
                        },
                        namedScheduleData = scheduleState.currentNamedScheduleData,
                        isPeriodic = scheduleState.currentNamedScheduleData.scheduleData?.scheduleEntity?.timetableType == TimetableType.PERIODIC,
                        scheduleView = appSettings.scheduleView
                    )
                }
            ) { padding ->
                if (scheduleUiState != null && scheduleState.currentNamedScheduleData.scheduleData?.scheduleEntity != null) {
                    CustomPullToRefreshBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = padding.calculateTopPadding()),
                        onRefresh = {
                            if (scheduleState.isSaved) {
                                scheduleViewModel.refreshScheduleState(
                                    showLoading = false,
                                    updating = true,
                                    namedScheduleId = scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.id
                                )
                            }
                        },
                        isRefreshing = scheduleState.isRefreshing
                    ) {
                        Column {
                            AnimatedAlert(
                                isHidden = scheduleState.isSaved,
                                title = stringResource(R.string.schedule_is_not_saved),
                                imageVector = ImageVector.vectorResource(R.drawable.alert),
                                backgroundColor = MaterialTheme.colorScheme.error,
                                actionTitle = stringResource(R.string.save),
                                action = {
                                    scheduleViewModel.saveCurrentNamedSchedule()
                                }
                            )
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
                                        ScheduleCalendarView(
                                            scheduleViewModel = scheduleViewModel,

                                            appUiState = appUiState,

                                            namedScheduleData = scheduleState.currentNamedScheduleData,
                                            scheduleData = scheduleState.currentNamedScheduleData.scheduleData,
                                            isSavedSchedule = scheduleState.isSaved,

                                            scheduleUiState = scheduleUiState,
                                            eventsCountView = appSettings.eventsCountView,
                                            eventView = appSettings.eventView,
                                            paddingBottom = externalPadding.calculateBottomPadding()
                                        )
                                    }

                                    ScheduleView.LIST -> {
                                        ScheduleListView(
                                            scheduleViewModel = scheduleViewModel,
                                            appBackStack = appUiState.appBackStack,

                                            scheduleUiState = scheduleUiState,

                                            isSavedSchedule = scheduleState.isSaved,
                                            namedScheduleEntity = scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity,
                                            scheduleData = scheduleState.currentNamedScheduleData.scheduleData,

                                            eventView = appSettings.eventView,
                                            paddingBottom = externalPadding.calculateBottomPadding()
                                        )
                                    }

                                    ScheduleView.SPLIT_WEEKS -> {
                                        if (scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.timetableType == TimetableType.PERIODIC && scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.recurrence != null) {
                                            ScheduleSplitWeeksView(
                                                scheduleViewModel = scheduleViewModel,

                                                appUiState = appUiState,
                                                scheduleUiState = scheduleUiState,

                                                namedScheduleData = scheduleState.currentNamedScheduleData,
                                                scheduleData = scheduleState.currentNamedScheduleData.scheduleData,
                                                recurrence = scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.recurrence,
                                                isSavedSchedule = scheduleState.isSaved,
                                                appSettings = appSettings,
                                                paddingBottom = externalPadding.calculateBottomPadding()
                                            )
                                        } else {
                                            settingsViewModel.onSetScheduleView(
                                                appSettings.scheduleView.next(
                                                    false
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (showDeleteNamedScheduleDialog) {
                        CustomAlertDialog(
                            dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                            dialogTitle = "${stringResource(R.string.delete_schedule)}?",
                            dialogText = stringResource(R.string.impossible_restore_eventextra),
                            onDismissRequest = {
                                showDeleteNamedScheduleDialog = false
                            },
                            onConfirmation = {
                                scheduleViewModel.deleteNamedSchedule(
                                    scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.id,
                                    scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.isDefault
                                )
                            }
                        )
                    }
                } else {
                    Empty(
                        title = "¯\\_(ツ)_/¯",
                        subtitle = stringResource(R.string.empty_here),
                        isBoldTitle = false,
                        paddingBottom = externalPadding.calculateBottomPadding()
                    )
                }
            }
        }

        scheduleState.savedNamedSchedules.isEmpty() -> {
            ErrorScreen(
                title = stringResource(R.string.no_saved_schedule),
                subtitle = stringResource(R.string.empty_base),
                button = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomButton(
                            modifier = Modifier.fillMaxWidth(),
                            buttonTitle = stringResource(R.string.find),
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            onClick = { appUiState.appBackStack.openDialog(Route.Dialog.SearchDialog) },
                        )
                        CustomButton(
                            modifier = Modifier.fillMaxWidth(),
                            buttonTitle = stringResource(R.string.create),
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            onClick = { appUiState.appBackStack.openDialog(Route.Dialog.AddScheduleDialog) },
                        )
                    }
                },
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        else -> {
            ErrorScreen(
                title = stringResource(R.string.error),
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }
    }
    showNamedScheduleDialog?.let {
        ModalDialogNamedSchedule(
            namedScheduleEntity = it,
            currentScheduleEntity = scheduleState.currentNamedScheduleData?.scheduleData?.scheduleEntity,
            schedules = scheduleState.currentNamedScheduleData?.namedSchedule?.schedules,
            today = currentDateTime.toLocalDate(),
            scheduleViewModel = scheduleViewModel,
            appBackStack = appUiState.appBackStack,

            isSavedNamedSchedule = scheduleState.isSaved,
            isDefaultNamedSchedule = it.isDefault,

            haveNotEmptySchedules = scheduleState.currentNamedScheduleData?.namedSchedule?.schedules?.isNotEmpty() == true && scheduleState.currentNamedScheduleData.scheduleData?.scheduleEntity != null
        ) {
            showNamedScheduleDialog = null
        }
    }

    if (showBackDialog) {
        CustomAlertDialog(
            dialogTitle = stringResource(R.string.return_default_schedule),
            dialogText = stringResource(R.string.do_you_want_continue),
            onDismissRequest = {
                showBackDialog = false
            },
            onConfirmation = {
                scheduleViewModel.refreshScheduleState(showLoading = false)
            }
        )
    }
}