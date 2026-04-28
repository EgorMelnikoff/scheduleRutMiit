package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.AnimatedAlert
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomPullToRefreshBox
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.ScheduleLoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.ModalDialogNamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.ScheduleTopAppBar
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.calendar.ScheduleCalendarView
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.list.ScheduleListView
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.AppUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.CurrentState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.ui.view_model.SettingsViewModel
import java.time.LocalDateTime

@Composable
fun ScreenSchedule(
    appUiState: AppUiState,
    currentState: CurrentState,
    namedScheduleState: NamedScheduleState,
    scheduleState: ScheduleState,
    scheduleUiState: ScheduleUiState?,
    appSettings: AppSettings,
    currentDateTime: LocalDateTime,
    scheduleViewModel: ScheduleViewModel,
    settingsViewModel: SettingsViewModel,
    externalPadding: PaddingValues
) {
    var showBackDialog by remember { mutableStateOf(false) }
    var showNamedScheduleDialog by remember { mutableStateOf<NamedSchedule?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf(false) }

    when {
        currentState.isLoading -> {
            BackHandler {
                scheduleViewModel.cancelLoading()
            }
            ScheduleLoadingScreen()
        }

        currentState.isError -> {
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

        namedScheduleState.namedScheduleWithSchedules != null -> {
            BackHandler(
                currentState.namedSchedules.isNotEmpty() && !requireNotNull(namedScheduleState.namedScheduleWithSchedules).namedSchedule.isDefault
            ) {
                showBackDialog = true
            }

            BackHandler(
                currentState.isRefreshing
            ) {
                scheduleViewModel.cancelRefresh()
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    ScheduleTopAppBar(
                        navigateToHiddenEvents = { namedSchedule ->
                            appUiState.appBackStack.openDialog(
                                Route.Dialog.HiddenEventsDialog(
                                    namedSchedule
                                )
                            )
                        },
                        onSetScheduleView = { value ->
                            settingsViewModel.onSetScheduleView(value)
                        },
                        onShowNamedScheduleDialog = { newValue ->
                            showNamedScheduleDialog = newValue
                        },
                        namedScheduleWithSchedules = requireNotNull(namedScheduleState.namedScheduleWithSchedules),
                        scheduleUiDto = scheduleState.scheduleUiDto,
                        scheduleView = appSettings.scheduleView
                    )
                }
            ) { padding ->
                if (scheduleUiState != null && scheduleState.scheduleUiDto?.schedule != null) {
                    CustomPullToRefreshBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = padding.calculateTopPadding()),
                        onRefresh = {
                            if (currentState.isSaved) {
                                scheduleViewModel.refreshScheduleState(
                                    showLoading = false,
                                    updating = true,
                                    namedScheduleId = requireNotNull(namedScheduleState.namedScheduleWithSchedules).namedSchedule.id
                                )
                            }
                        },
                        isRefreshing = currentState.isRefreshing
                    ) {
                        Column {
                            AnimatedAlert(
                                isHidden = currentState.isSaved
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(
                                            MaterialTheme.colorScheme.error
                                        )
                                ) {
                                    ClickableItem(
                                        verticalPadding = 12.dp,
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
                                            Text(
                                                modifier = Modifier.padding(end = 4.dp),
                                                text = stringResource(R.string.save),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        },
                                        showClickLabel = false
                                    ) {
                                        scheduleViewModel.saveCurrentNamedSchedule()
                                    }
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
                                        ScheduleCalendarView(
                                            scheduleViewModel = scheduleViewModel,

                                            appUiState = appUiState,

                                            namedScheduleWithSchedules = requireNotNull(namedScheduleState.namedScheduleWithSchedules),
                                            scheduleUiDto = requireNotNull(scheduleState.scheduleUiDto),
                                            isSavedSchedule = currentState.isSaved,

                                            scheduleUiState = scheduleUiState,
                                            appSettings = appSettings,
                                            paddingBottom = externalPadding.calculateBottomPadding()
                                        )
                                    }

                                    ScheduleView.LIST -> {
                                        ScheduleListView(
                                            scheduleViewModel = scheduleViewModel,
                                            appBackStack = appUiState.appBackStack,

                                            scheduleUiState = scheduleUiState,

                                            isSavedSchedule = currentState.isSaved,
                                            namedSchedule = requireNotNull(namedScheduleState.namedScheduleWithSchedules).namedSchedule,
                                            scheduleUiDto = requireNotNull(scheduleState.scheduleUiDto),

                                            appSettings = appSettings,
                                            paddingBottom = externalPadding.calculateBottomPadding()
                                        )
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
                                    requireNotNull(namedScheduleState.namedScheduleWithSchedules).namedSchedule.id,
                                    requireNotNull(namedScheduleState.namedScheduleWithSchedules).namedSchedule.isDefault
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

        currentState.namedSchedules.isEmpty() -> {
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
            namedSchedule = it,
            currentSchedule = scheduleState.scheduleUiDto?.schedule,
            scheduleWithEvents = namedScheduleState.namedScheduleWithSchedules?.scheduleWithEvents,
            today = currentDateTime.toLocalDate(),
            scheduleViewModel = scheduleViewModel,
            appBackStack = appUiState.appBackStack,

            isSavedNamedSchedule = currentState.isSaved,
            isDefaultNamedSchedule = it.isDefault,

            haveNotEmptySchedules = namedScheduleState.namedScheduleWithSchedules?.scheduleWithEvents?.isNotEmpty() == true && scheduleState.scheduleUiDto?.schedule != null
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