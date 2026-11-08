package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.extension.getStartMs
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.chip.AppAssistChip
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findDefault
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common.ModalDialogNamedScheduleHeader
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleViewModel
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSheetWidget(
    namedSchedule: NamedSchedule,
    currentSchedule: Schedule? = null,
    schedules: List<Schedule>,

    scheduleViewModel: ScheduleViewModel,
    isSavedNamedSchedule: Boolean,
    haveHiddenEvents: Boolean = false,
    haveNotEmptySchedules: Boolean = false,

    onOpenDialog: (Route.Dialog) -> Unit,
    onDeleteNamedSchedule: () -> Unit,
    onDismiss: (NamedSchedule?) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    AppBottomSheet(
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding16),
        onDismiss = {
            onDismiss(null)
        }
    ) {
        ModalDialogNamedScheduleHeader(
            onDeleteNamedSchedule = onDeleteNamedSchedule,
            namedSchedule = namedSchedule,
            isSavedNamedSchedule = isSavedNamedSchedule,
            schedule = schedules.findDefault(),
            onOpenRenameDialog = {
                onOpenDialog(Route.Dialog.RenameNamedScheduleDialog(namedSchedule.id))
            },
            onDismiss = { onDismiss(null) }
        )
        currentSchedule?.let { schedule ->
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(
                        horizontal = AppTheme.paddings.padding16
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
            ) {
                AnimatedVisibility(
                    visible = schedule.downloadUrl != null,
                ) {
                    AppAssistChip(
                        imageVector = ImageVector.vectorResource(R.drawable.download),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = AppTheme.colors.background,
                            labelColor = AppTheme.colors.onBackground,
                            leadingIconContentColor = AppTheme.colors.onSecondaryContainer
                        ),
                        border = BorderStroke(
                            color = AppTheme.colors.outline,
                            width = 0.5.dp
                        ),
                        title = stringResource(R.string.download_pdf),
                        onClick = {
                            schedule.downloadUrl?.let { url ->
                                uriHandler.openUri(url)
                            }
                        }
                    )
                }
                AnimatedVisibility(
                    visible = isSavedNamedSchedule && haveNotEmptySchedules
                ) {
                    AppAssistChip(
                        imageVector = ImageVector.vectorResource(R.drawable.add),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = AppTheme.colors.background,
                            labelColor = AppTheme.colors.onBackground,
                            leadingIconContentColor = AppTheme.colors.onSecondaryContainer
                        ),
                        border = BorderStroke(
                            color = AppTheme.colors.outline,
                            width = 0.5.dp
                        ),
                        title = stringResource(R.string.add_class),
                        onClick = {
                            onDismiss(null)
                            onOpenDialog(
                                Route.Dialog.EditEventDialog(
                                    eventId = null,
                                    scheduleId = schedule.id
                                )
                            )
                        }
                    )
                }
                AnimatedVisibility(
                    visible = haveHiddenEvents
                ) {
                    AppAssistChip(
                        imageVector = ImageVector.vectorResource(R.drawable.visibility_off),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = AppTheme.colors.background,
                            labelColor = AppTheme.colors.onBackground,
                            leadingIconContentColor = AppTheme.colors.onSecondaryContainer
                        ),
                        border = BorderStroke(
                            color = AppTheme.colors.outline,
                            width = 0.5.dp
                        ),
                        title = stringResource(R.string.hidden_events),
                        onClick = {
                            onDismiss(null)
                            onOpenDialog(
                                Route.Dialog.HiddenEventsDialog(schedule.id)
                            )
                        }
                    )
                }
            }
        }

        if (namedSchedule.type != NamedScheduleType.MY) {
            ColumnGroup(
                modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
                items = schedules.map { schedule ->
                    {
                        AppCell(
                            defaultMinHeight = 40.dp,
                            title = schedule.timetableType.typeName,
                            subtitle = "${schedule.startDate.format(dayMonthYearFormatter)} - " +
                                    "${schedule.endDate.format(dayMonthYearFormatter)}",
                            onClick = {
                                if (schedules.size > 1) {
                                    scheduleViewModel.setDefaultSchedule(
                                        schedule.id,
                                        schedule.timetableId
                                    )
                                }
                            },
                            leadingIcon = if (schedules.size > 1) {
                                {
                                    RadioButton(
                                        selected = (schedule.id == currentSchedule?.id && isSavedNamedSchedule)
                                                || schedule.isDefault,
                                        onClick = {
                                            scheduleViewModel.setDefaultSchedule(
                                                schedule.id,
                                                schedule.timetableId
                                            )
                                        }
                                    )
                                }
                            } else null,
                            trailingIcon = {
                                val progress = remember(schedule) {
                                    calculateProgress(
                                        startDate = schedule.startDate,
                                        endDate = schedule.endDate
                                    )
                                }

                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    progress = { progress },
                                    color = AppTheme.colors.primary,
                                    trackColor = AppTheme.colors.primaryContainer,
                                    strokeWidth = 3.dp
                                )
                            },
                            showClickLabel = false,
                            verticalPadding = 8.dp
                        )
                    }
                }
            )
        }
    }
}


fun calculateProgress(
    startDate: LocalDate,
    endDate: LocalDate,
    currentDate: LocalDate = LocalDate.now()
): Float {
    val zone = ZoneId.systemDefault()

    val startMs = startDate.getStartMs(zone)
    val endMs = endDate.getStartMs(zone)
    val currentMs = currentDate.getStartMs(zone)

    if (currentMs <= startMs) return 0.0f
    if (currentMs >= endMs) return 1.0f

    val totalDuration = endMs - startMs
    val elapsedDuration = currentMs - startMs

    return elapsedDuration.toFloat() / totalDuration.toFloat()
}