package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
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
    var showNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf(false) }

    when {
        scheduleState.isLoading -> {
            LoadingScreen(
                paddingTop = externalPadding.calculateTopPadding(),
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        scheduleState.isError -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.error),
                subtitle = LocalContext.current.getString(R.string.error_load_schedule),
                paddingTop = externalPadding.calculateTopPadding(),
                button = {
                    CustomButton(
                        modifier = Modifier.fillMaxWidth(),
                        buttonTitle = LocalContext.current.getString(R.string.return_default),
                        imageVector = ImageVector.vectorResource(R.drawable.back),
                        onClick = { scheduleActions.onLoadInitialScheduleData() },
                    )
                },
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        scheduleState.currentNamedScheduleData?.namedSchedule != null -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    ScheduleTopAppBar(
                        navigateToAddEvent = navigationActions.navigateToAddEvent,
                        onSetScheduleView = { value ->
                            settingsViewModel.onSetScheduleView(value)
                        },
                        onShowExpandedMenu = scheduleUiState?.onExpandSchedulesMenu,
                        onShowNamedScheduleDialog = { newValue ->
                            showNamedScheduleDialog = newValue
                        },
                        namedScheduleData = scheduleState.currentNamedScheduleData,
                        calendarView = appSettings.calendarView,
                        expandedSchedulesMenu = scheduleUiState?.expandedSchedulesMenu
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
                            ExpandedMenu(
                                setDefaultSchedule = scheduleActions.onSetDefaultSchedule,
                                scheduleState = scheduleState,
                                expandedSchedulesMenu = scheduleUiState.expandedSchedulesMenu,
                                onShowExpandedMenu = scheduleUiState.onExpandSchedulesMenu
                            )
                            AnimatedContent(
                                targetState = appSettings.calendarView,
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
                            dialogTitle = "${LocalContext.current.getString(R.string.delete_schedule)}?",
                            dialogText = LocalContext.current.getString(R.string.impossible_restore_eventextra),
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
                        subtitle = LocalContext.current.getString(R.string.empty_here),
                        isBoldTitle = false,
                        paddingBottom = externalPadding.calculateBottomPadding()
                    )
                }
            }
        }

        scheduleState.savedNamedSchedules.isEmpty() -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                button = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomButton(
                            modifier = Modifier.fillMaxWidth(),
                            buttonTitle = LocalContext.current.getString(R.string.find),
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            onClick = { navigationActions.navigateToSearch() },
                        )
                        CustomButton(
                            modifier = Modifier.fillMaxWidth(),
                            buttonTitle = LocalContext.current.getString(R.string.create),
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
                title = LocalContext.current.getString(R.string.error),
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }
    }
    showNamedScheduleDialog?.let {
        ModalDialogNamedSchedule(
            namedScheduleEntity = showNamedScheduleDialog!!,
            scheduleData = scheduleState.currentNamedScheduleData?.scheduleData,
            onDownloadCurrentSchedule = if (scheduleState.currentNamedScheduleData?.scheduleData?.scheduleEntity?.downloadUrl != null) {
                {
                    appUiState.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.downloadUrl.toUri()
                        )
                    )
                }
            } else null,
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
            onDeleteNamedSchedule = {
                showDeleteNamedScheduleDialog = true
            },
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

}

@Composable
fun IsSavedAlert(
    isSaved: Boolean,
    onSave: () -> Unit
) {
    AnimatedVisibility(
        visible = !isSaved,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
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
                text = LocalContext.current.getString(R.string.schedule_is_not_saved),
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
                        text = LocalContext.current.getString(R.string.save),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}