package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomPullToRefreshBox
import com.egormelnikoff.schedulerutmiit.ui.elements.ModalDialogNamedSchedule
import com.egormelnikoff.schedulerutmiit.ui.elements.ScheduleTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState

@Composable
fun ScreenSchedule(
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    navigateToAddSchedule: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAddEvent: (ScheduleEntity) -> Unit,
    navigateToRenameDialog: (NamedScheduleEntity) -> Unit,

    onDeleteEvent: (Long) -> Unit,
    onHideEvent: (Long) -> Unit,

    onLoadInitialData: () -> Unit,
    onRefreshState: (Long) -> Unit,
    navigateToHiddenEvents: () -> Unit,
    onSaveCurrentNamedSchedule: () -> Unit,
    onSelectDefaultNamedSchedule: (Long) -> Unit,
    onDeleteNamedSchedule: (Pair<Long, Boolean>) -> Unit,
    onSetDefaultSchedule: (Triple<Long, Long, String>) -> Unit,
    onSetScheduleView: (Boolean) -> Unit,

    scheduleUiState: ScheduleUiState,
    scheduleState: ScheduleState?,
    appSettings: AppSettings,
    externalPadding: PaddingValues
) {
    var showNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf(false) }

    when {
        scheduleUiState.isLoading -> {
            LoadingScreen(
                paddingTop = externalPadding.calculateTopPadding(),
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        scheduleUiState.isError -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.error),
                subtitle = LocalContext.current.getString(R.string.error_load_schedule),
                paddingTop = externalPadding.calculateTopPadding(),
                button = {
                    CustomButton(
                        buttonTitle = LocalContext.current.getString(R.string.return_default),
                        imageVector = ImageVector.vectorResource(R.drawable.back),
                        onClick = { onLoadInitialData() },
                    )
                },
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        scheduleUiState.currentNamedScheduleData?.namedSchedule != null -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    ScheduleTopAppBar(
                        navigateToAddEvent = navigateToAddEvent,
                        onSetScheduleView = onSetScheduleView,
                        onShowExpandedMenu = scheduleState?.onExpandSchedulesMenu,
                        onShowNamedScheduleDialog = { newValue ->
                            showNamedScheduleDialog = newValue
                        },
                        scheduleUiState = scheduleUiState,
                        calendarView = appSettings.calendarView,
                        expandedSchedulesMenu = scheduleState?.expandedSchedulesMenu,
                        context = LocalContext.current
                    )
                }
            ) { padding ->
                if (scheduleState != null && scheduleUiState.currentNamedScheduleData.settledScheduleEntity != null) {
                    CustomPullToRefreshBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = padding.calculateTopPadding()),
                        isRefreshing = scheduleUiState.isUpdating,
                        onRefresh = {
                            onRefreshState(scheduleUiState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.id)
                        },
                    ) {
                        Column {
                            IsSavedAlert(
                                isSaved = scheduleUiState.isSaved,
                                onSave = onSaveCurrentNamedSchedule
                            )
                            ExpandedMenu(
                                setDefaultSchedule = onSetDefaultSchedule,
                                scheduleUiState = scheduleUiState,
                                expandedSchedulesMenu = scheduleState.expandedSchedulesMenu,
                                onShowExpandedMenu = scheduleState.onExpandSchedulesMenu
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
                                        navigateToEvent = navigateToEvent,
                                        onDeleteEvent = onDeleteEvent,
                                        onUpdateHiddenEvent = onHideEvent,

                                        scheduleUiState = scheduleUiState,
                                        scheduleState = scheduleState,
                                        isShowCountClasses = appSettings.showCountClasses,
                                        isShortEvent = appSettings.eventView,
                                        paddingBottom = externalPadding.calculateBottomPadding()
                                    )
                                } else {
                                    ScheduleListView(
                                        navigateToEvent = navigateToEvent,
                                        onDeleteEvent = onDeleteEvent,
                                        onUpdateHiddenEvent = onHideEvent,

                                        scheduleUiState = scheduleUiState,
                                        scheduleState = scheduleState,
                                        isShortEvent = appSettings.eventView,
                                        paddingBottom = externalPadding.calculateBottomPadding()
                                    )
                                }
                            }
                        }
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

        scheduleUiState.savedNamedSchedules.isEmpty() -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                button = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomButton(
                            buttonTitle = LocalContext.current.getString(R.string.find),
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            onClick = { navigateToSearch() },
                        )
                        CustomButton(
                            buttonTitle = LocalContext.current.getString(R.string.create),
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            onClick = { navigateToAddSchedule() },
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
        if (scheduleUiState.isSaved) {
            ModalDialogNamedSchedule(
                namedScheduleEntity = showNamedScheduleDialog!!,
                scheduleEntity = scheduleUiState.currentNamedScheduleData?.settledScheduleEntity,
                navigateToRenameDialog = {
                    navigateToRenameDialog(showNamedScheduleDialog!!)
                },
                navigateToHiddenEvents = if (!scheduleUiState.currentNamedScheduleData?.hiddenEvents.isNullOrEmpty()) {
                    navigateToHiddenEvents
                } else null,
                onDismiss = {
                    showNamedScheduleDialog = null
                },
                onSetDefaultNamedSchedule = if (!showNamedScheduleDialog!!.isDefault) {
                    { onSelectDefaultNamedSchedule(showNamedScheduleDialog!!.id) }
                } else null,
                onDeleteNamedSchedule = {
                    showDeleteNamedScheduleDialog = true
                },
                onLoadInitialData = if (!showNamedScheduleDialog!!.isDefault) {
                    onLoadInitialData
                } else null

            )
        } else {
            ModalDialogNamedSchedule(
                namedScheduleEntity = showNamedScheduleDialog!!,
                scheduleEntity = scheduleUiState.currentNamedScheduleData?.settledScheduleEntity,
                onDismiss = {
                    showNamedScheduleDialog = null
                },
                onSaveCurrentNamedSchedule = onSaveCurrentNamedSchedule,
                onLoadInitialData = if (!showNamedScheduleDialog!!.isDefault) {
                    onLoadInitialData
                } else null,

                )
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
                onDeleteNamedSchedule(
                    Pair(
                        scheduleUiState.currentNamedScheduleData!!.namedSchedule!!.namedScheduleEntity.id,
                        scheduleUiState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.isDefault
                    )
                )
            }
        )
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
                IconButton(
                    onClick = onSave
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.save),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}