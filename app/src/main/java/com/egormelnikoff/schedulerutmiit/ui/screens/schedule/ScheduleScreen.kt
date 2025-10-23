package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.dialogs.DialogNamedScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.ScheduleTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState
import java.time.LocalDate

@Composable
fun ScreenSchedule(
    externalPadding: PaddingValues,
    today: LocalDate,

    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    navigateToAddSchedule: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAddEvent: (ScheduleEntity) -> Unit,

    onDeleteEvent: (Long) -> Unit,
    onHideEvent: (Long) -> Unit,

    onLoadInitialData: () -> Unit,
    onSaveCurrentNamedSchedule: () -> Unit,
    onSelectDefaultNamedSchedule: (Long) -> Unit,
    onDeleteNamedSchedule: (Pair<Long, Boolean>) -> Unit,
    onSetDefaultSchedule: (Triple<Long, Long, String>) -> Unit,

    onSetScheduleView: (Boolean) -> Unit,
    onShowExpandedMenu: (Boolean) -> Unit,

    expandedSchedulesMenu: Boolean,
    appSettings: AppSettings,

    scheduleUiState: ScheduleUiState,
    scheduleCalendarState: ScheduleCalendarState,
    scheduleListState: LazyListState,
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

        scheduleUiState.currentScheduleData?.namedSchedule != null -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    ScheduleTopAppBar(
                        navigateToAddEvent = navigateToAddEvent,
                        onSetScheduleView = onSetScheduleView,
                        onShowExpandedMenu = onShowExpandedMenu,
                        onShowNamedScheduleDialog = { newValue ->
                            showNamedScheduleDialog = newValue
                        },
                        scheduleUiState = scheduleUiState,
                        calendarView = appSettings.calendarView,
                        expandedSchedulesMenu = expandedSchedulesMenu,
                        context = LocalContext.current
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = padding.calculateTopPadding())
                ) {
                    ExpandedMenu(
                        setDefaultSchedule = onSetDefaultSchedule,
                        scheduleUiState = scheduleUiState,
                        expandedSchedulesMenu = expandedSchedulesMenu,
                        onShowExpandedMenu = onShowExpandedMenu
                    )
                    if (scheduleUiState.currentScheduleData.settledScheduleEntity != null) {
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
                                    isSavedSchedule = scheduleUiState.isSaved,
                                    isShowCountClasses = appSettings.showCountClasses,
                                    isShortEvent = appSettings.eventView,

                                    scheduleUiState = scheduleUiState,
                                    today = today,
                                    scheduleCalendarState = scheduleCalendarState,
                                    paddingBottom = externalPadding.calculateBottomPadding()
                                )
                            } else {
                                ScheduleListView(
                                    navigateToEvent = navigateToEvent,
                                    onDeleteEvent = onDeleteEvent,
                                    onUpdateHiddenEvent = onHideEvent,
                                    isSavedSchedule = scheduleUiState.isSaved,
                                    recurrence = scheduleUiState.currentScheduleData.settledScheduleEntity.recurrence,
                                    startDate = scheduleUiState.currentScheduleData.settledScheduleEntity.startDate,
                                    eventsForList = scheduleUiState.currentScheduleData.eventForList,
                                    eventsExtraData = scheduleUiState.currentScheduleData.eventsExtraData,
                                    scheduleListState = scheduleListState,
                                    isShortEvent = appSettings.eventView,
                                    paddingBottom = externalPadding.calculateBottomPadding(),
                                )
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
                            buttonTitle = LocalContext.current.getString(R.string.search),
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
                paddingTop = externalPadding.calculateTopPadding(),
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        else -> {
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
    }

    if (showNamedScheduleDialog != null) {
        DialogNamedScheduleActions(
            namedScheduleEntity = showNamedScheduleDialog!!,
            onSelectDefault = {
                onSelectDefaultNamedSchedule(showNamedScheduleDialog!!.id)
            },
            onDelete = {
                showDeleteNamedScheduleDialog = true
            },
            onDismiss = {
                showNamedScheduleDialog = null
            },
            onLoadInitialData = if (!scheduleUiState.currentScheduleData!!.namedSchedule!!.namedScheduleEntity.isDefault) {
                onLoadInitialData
            } else null,
            isSavedNamedSchedule = scheduleUiState.isSaved,
            onSaveCurrentNamedSchedule = onSaveCurrentNamedSchedule
        )
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
                        scheduleUiState.currentScheduleData!!.namedSchedule!!.namedScheduleEntity.id,
                        scheduleUiState.currentScheduleData.namedSchedule.namedScheduleEntity.isDefault
                    )
                )
            }
        )
    }
}