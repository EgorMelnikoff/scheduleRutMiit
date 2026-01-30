package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomPullToRefreshBox
import com.egormelnikoff.schedulerutmiit.ui.elements.ModalDialogNamedSchedule
import com.egormelnikoff.schedulerutmiit.ui.elements.ScheduleTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

@Composable
fun ScreenSchedule(
    appUiState: AppUiState,
    scheduleState: ScheduleState,
    scheduleUiState: ScheduleUiState?,
    appSettings: AppSettings,
    navigationActions: NavigationActions,
    scheduleActions: ScheduleActions,
    settingsViewModel: SettingsViewModel,
    externalPadding: PaddingValues
) {
    var showBackDialog by remember { mutableStateOf(false) }
    var showNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf(false) }

    when {
        scheduleState.isLoading -> {
            BackHandler {
                scheduleActions.cancelLoading()
            }
            LoadingScreen(
                paddingTop = externalPadding.calculateTopPadding(),
                paddingBottom = externalPadding.calculateBottomPadding()
            )
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
                        onClick = { scheduleActions.onLoadInitialScheduleData() },
                    )
                },
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        scheduleState.currentNamedScheduleData?.namedSchedule != null -> {
            if (!scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.isDefault) {
                BackHandler {
                    showBackDialog = true
                }
            }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    ScheduleTopAppBar(
                        navigateToAddEvent = navigationActions.navigateToAddEvent,
                        onSetScheduleView = { value ->
                            settingsViewModel.onSetScheduleView(value)
                        },
                        onShowNamedScheduleDialog = { newValue ->
                            showNamedScheduleDialog = newValue
                        },
                        namedScheduleData = scheduleState.currentNamedScheduleData,
                        isSavedSchedule = scheduleState.isSaved,
                        calendarView = appSettings.scheduleView
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
                                scheduleActions.onRefreshScheduleState(scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.id)
                            }
                        },
                        isRefreshing = scheduleState.isUpdating
                    ) {
                        Column {
                            IsSavedAlert(
                                isSaved = scheduleState.isSaved,
                                onSave = scheduleActions.onSaveCurrentNamedSchedule
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
                                if (targetState) {
                                    ScheduleCalendarView(
                                        navigateToEvent = navigationActions.navigateToEvent,
                                        navigateToEditEvent = navigationActions.navigateToEditEvent,
                                        onDeleteEvent = scheduleActions.eventActions.onDeleteEvent,
                                        onUpdateHiddenEvent = scheduleActions.eventActions.onHideEvent,

                                        appUiState = appUiState,
                                        scheduleState = scheduleState,
                                        scheduleUiState = scheduleUiState,
                                        isShowCountClasses = appSettings.showCountClasses,
                                        eventView = appSettings.eventView,
                                        paddingBottom = externalPadding.calculateBottomPadding()
                                    )
                                } else {
                                    ScheduleListView(
                                        navigateToEvent = navigationActions.navigateToEvent,
                                        navigateToEditEvent = navigationActions.navigateToEditEvent,
                                        onDeleteEvent = scheduleActions.eventActions.onDeleteEvent,
                                        onUpdateHiddenEvent = scheduleActions.eventActions.onHideEvent,

                                        scheduleState = scheduleState,
                                        scheduleUiState = scheduleUiState,
                                        eventView = appSettings.eventView,
                                        paddingBottom = externalPadding.calculateBottomPadding()
                                    )
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
                                scheduleActions.onDeleteNamedSchedule(
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
                            onClick = { navigationActions.navigateToSearch() },
                        )
                        CustomButton(
                            modifier = Modifier.fillMaxWidth(),
                            buttonTitle = stringResource(R.string.create),
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            onClick = { navigationActions.navigateToAddSchedule() },
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
            namedScheduleEntity = showNamedScheduleDialog!!,
            appUiState = appUiState,
            scheduleActions = scheduleActions,
            namedScheduleData = scheduleState.currentNamedScheduleData,
            navigateToRenameDialog = if (scheduleState.isSaved) {
                { navigationActions.navigateToRenameDialog(showNamedScheduleDialog!!) }
            } else null,
            navigateToHiddenEvents = if (!scheduleState.currentNamedScheduleData?.scheduleData?.hiddenEvents.isNullOrEmpty() && scheduleState.isSaved) {
                navigationActions.navigateToHiddenEvents
            } else null,
            onSetDefaultNamedSchedule =
                if (!showNamedScheduleDialog!!.isDefault && scheduleState.isSaved) {
                    {
                        scheduleActions.onSelectDefaultNamedSchedule(
                            showNamedScheduleDialog!!.id
                        )
                    }
                } else null,
            onDeleteNamedSchedule = if (scheduleState.isSaved) {
                { showDeleteNamedScheduleDialog = true }
            } else null,
            onSaveCurrentNamedSchedule = if (!scheduleState.isSaved) {
                scheduleActions.onSaveCurrentNamedSchedule
            } else null,
            onLoadInitialData = if (!showNamedScheduleDialog!!.isDefault) {
                scheduleActions.onLoadInitialScheduleData
            } else null
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
                scheduleActions.onLoadInitialScheduleData()
            }
        )
    }

}

@Composable
fun IsSavedAlert(
    isSaved: Boolean,
    onSave: () -> Unit
) {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        AnimatedVisibility(
            visible = !isSaved,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            ColumnGroup(
                items = listOf {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.error)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.alert),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.schedule_is_not_saved),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                            TextButton(
                                onClick = onSave,
                                shape = MaterialTheme.shapes.small,
                                interactionSource = null
                            ) {
                                Text(
                                    text = stringResource(R.string.save),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            )
        }

    }
}