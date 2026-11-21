package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppAlertBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarData
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.rememberCalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.containers.GridGroup
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ErrorScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleUiState

@Composable
fun ScheduleScreenWidget(
    state: ScheduleUiState,
    contentPadding: PaddingValues,
    onAction: (ScheduleAction) -> Unit
) {
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        onAction(ScheduleAction.OnImportData(uri))
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { onAction(ScheduleAction.OnExportData(it)) }
    }

    val scheduleListState = rememberLazyListState()
    val scheduleCalendarState = rememberScheduleCalendarState(state.scheduleVo?.calendarData)

    when (state.mode) {
        is ScheduleUiState.Mode.Loading -> {
            if (state.scheduleVo != null) {
                BackHandler {
                    onAction(ScheduleAction.OnCancelLoading)
                }
            }
            ScheduleLoadingWidget()
        }

        is ScheduleUiState.Mode.Empty -> {
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
                                            onAction(ScheduleAction.OnOpenDialog(Route.Dialog.SearchDialog))
                                        },
                                    )
                                },
                                { shape ->
                                    AppButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        buttonTitle = stringResource(R.string.create),
                                        imageVector = ImageVector.vectorResource(R.drawable.add),
                                        shape = shape,
                                        onClick = {
                                            onAction(ScheduleAction.OnOpenDialog(Route.Dialog.AddScheduleDialog))
                                        },
                                    )
                                }
                            ),
                            listOf { shape ->
                                AppButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    buttonTitle = stringResource(R.string._import),
                                    imageVector = ImageVector.vectorResource(R.drawable.resource_import),
                                    shape = shape,
                                    onClick = {
                                        importLauncher.launch(arrayOf("application/json"))
                                    },
                                )
                            }
                        )
                    )
                },
                paddingBottom = contentPadding.calculateBottomPadding() - 16.dp
            )
        }

        is ScheduleUiState.Mode.Error -> {
            ErrorScreen(
                title = stringResource(R.string.error),
                subtitle = stringResource(R.string.error_load_schedule),
                paddingTop = contentPadding.calculateTopPadding(),
                button = {
                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        buttonTitle = stringResource(R.string.return_default),
                        imageVector = ImageVector.vectorResource(R.drawable.back),
                        onClick = {
                            onAction(ScheduleAction.OnReturnToDefault)
                        },
                    )
                },
                paddingBottom = contentPadding.calculateBottomPadding()
            )
        }

        is ScheduleUiState.Mode.Content -> {
            val scheduleVo = state.scheduleVo
            val namedSchedule = state.namedSchedule

            if (scheduleVo == null || namedSchedule == null) {
                EmptyScreen(
                    title = "¯\\_(ツ)_/¯",
                    subtitle = stringResource(R.string.empty_here),
                    isBoldTitle = false,
                    paddingBottom = contentPadding.calculateBottomPadding()
                )
            } else {
                ScheduleContentWidget(
                    state = state,
                    namedSchedule = namedSchedule,
                    scheduleVo = scheduleVo,
                    scheduleCalendarState = scheduleCalendarState,
                    scheduleListState = scheduleListState,
                    contentPadding = contentPadding,
                    onAction = onAction
                )
            }
        }
    }

    if (state.schedulesSheet) {
        SchedulesSheetWidget(
            namedSchedules = state.namedSchedules,
            currentNamedScheduleId = state.namedSchedule?.id,
            onAction = onAction,
            onExportClick = {
                exportLauncher.launch("backup")
            }
        )
    }

    if (state.importSheet) {
        AppAlertBottomSheet(
            onDismissRequest = {
                onAction(ScheduleAction.OnDismissImportSheet)
            },
            onConfirmation = {
                onAction(ScheduleAction.OnDismissImportSheet)
                importLauncher.launch(arrayOf("application/json"))
            },
            dialogTitle = stringResource(R.string._import) + "?",
            dialogText = stringResource(R.string.import_message)
        )
    }

    if (state.returnScheduleSheet) {
        AppAlertBottomSheet(
            dialogTitle = stringResource(R.string.return_default_schedule),
            dialogText = stringResource(R.string.do_you_want_continue),
            confirmText = "Вернуть",
            onDismissRequest = {
                onAction(ScheduleAction.OnDismissReturnToDefault)
            },
            onConfirmation = {
                onAction(ScheduleAction.OnReturnToDefault)
            }
        )
    }
}

@Composable
private fun rememberScheduleCalendarState(
    calendarData: CalendarData?
): CalendarState? {
    return if (calendarData != null) {
        rememberCalendarState(calendarData)
    } else {
        null
    }
}
