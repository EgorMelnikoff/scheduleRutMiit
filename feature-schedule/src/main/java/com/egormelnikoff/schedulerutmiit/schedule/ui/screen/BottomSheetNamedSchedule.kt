package com.egormelnikoff.schedulerutmiit.schedule.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomFilterChip
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogNamedSchedule(
    namedSchedule: NamedSchedule,
    currentSchedule: Schedule? = null,
    schedulesWithEvents: List<ScheduleWithEvents>? = null,

    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,
    today: LocalDate? = null,
    isDarkTheme: Boolean? = null,
    isSavedNamedSchedule: Boolean,
    isDefaultNamedSchedule: Boolean,
    haveHiddenEvents: Boolean = false,
    haveNotEmptySchedules: Boolean = false,

    onOpenNamedSchedule: ((Long, Boolean, Boolean) -> Unit)? = null,
    onDismiss: (NamedSchedule?) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    CustomModalBottomSheet(
        isDarkTheme = isDarkTheme,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        onDismiss = {
            onDismiss(null)
        }
    ) {
        ModalDialogNamedScheduleHeader(
            appBackStack = appBackStack,
            scheduleViewModel = scheduleViewModel,
            namedSchedule = namedSchedule,
            isSavedNamedSchedule = isSavedNamedSchedule,
            isDefaultNamedSchedule = isDefaultNamedSchedule,
            onDismiss = onDismiss
        )
        if (schedulesWithEvents == null) {
            ColumnGroup(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = buildList {
                    if (isSavedNamedSchedule && !isDefaultNamedSchedule && onOpenNamedSchedule != null) {
                        add {
                            ClickableItem(
                                title = stringResource(R.string.make_default),
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.check),
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        contentDescription = null
                                    )
                                },
                                defaultMinHeight = 24.dp,
                                showClickLabel = false
                            ) {
                                onOpenNamedSchedule(namedSchedule.id, true, false)
                                onDismiss(null)
                            }

                        }
                    }
                    onOpenNamedSchedule?.let {
                        add {
                            ClickableItem(
                                title = stringResource(R.string.open),
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.open_panel),
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        contentDescription = null
                                    )
                                },
                                defaultMinHeight = 24.dp
                            ) {
                                onOpenNamedSchedule(namedSchedule.id, false, true)
                                onDismiss(null)
                            }
                        }
                    }
                }
            )
        }
        currentSchedule?.let { schedule ->
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(
                        horizontal = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (schedule.downloadUrl != null) {
                    CustomFilterChip(
                        imageVector = ImageVector.vectorResource(R.drawable.download),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            labelColor = MaterialTheme.colorScheme.onBackground,
                            iconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = BorderStroke(
                            color = MaterialTheme.colorScheme.outline,
                            width = 0.5.dp
                        ),
                        title = stringResource(R.string.download),
                        onClick = {
                            val url = schedule.downloadUrl!!
                            uriHandler.openUri(url)
                        }
                    )
                }
                if (isSavedNamedSchedule && haveNotEmptySchedules) {
                    CustomFilterChip(
                        imageVector = ImageVector.vectorResource(R.drawable.add),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            labelColor = MaterialTheme.colorScheme.onBackground,
                            iconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = BorderStroke(
                            color = MaterialTheme.colorScheme.outline,
                            width = 0.5.dp
                        ),
                        title = stringResource(R.string.add_class),
                        onClick = {
                            onDismiss(null)
                            appBackStack.openDialog(
                                Route.Dialog.AddEditEventDialog(
                                    namedScheduleId = namedSchedule.id,
                                    scheduleId = schedule.id,
                                    recurrence = schedule.recurrence,
                                    scheduleStartDate = schedule.startDate,
                                    scheduleEndDate = schedule.endDate
                                )
                            )
                        }
                    )
                }
                if (haveHiddenEvents) {
                    CustomFilterChip(
                        imageVector = ImageVector.vectorResource(R.drawable.visibility_off),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            labelColor = MaterialTheme.colorScheme.onBackground,
                            iconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = BorderStroke(
                            color = MaterialTheme.colorScheme.outline,
                            width = 0.5.dp
                        ),
                        title = stringResource(R.string.hidden_events),
                        onClick = {
                            onDismiss(null)
                            appBackStack.openDialog(
                                Route.Dialog.HiddenEventsDialog(
                                    namedScheduleId = namedSchedule.id,
                                    namedScheduleShortName = namedSchedule.shortName,
                                    timetableType = schedule.timetableType
                                )
                            )
                        }
                    )
                }

                if (isSavedNamedSchedule && schedule.endDate < today) {
                    CustomFilterChip(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            labelColor = MaterialTheme.colorScheme.error,
                            iconColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(
                            color = MaterialTheme.colorScheme.outline,
                            width = 0.5.dp
                        ),
                        title = stringResource(R.string.delete_schedule),
                        onClick = {
                            scheduleViewModel.deleteSchedule(
                                schedule.namedScheduleId,
                                schedule.id
                            )

                            onDismiss(null)
                        }
                    )
                }
            }
        }

        schedulesWithEvents?.let {
            ColumnGroup(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = schedulesWithEvents.map { scheduleWithEvents ->
                    {
                        Column {
                            ClickableItem(
                                defaultMinHeight = 40.dp,
                                title = scheduleWithEvents.schedule.timetableType.typeName,
                                subtitle = "${
                                    scheduleWithEvents.schedule.startDate.format(
                                        dayMonthYearFormatter
                                    )
                                } - " +
                                        "${
                                            scheduleWithEvents.schedule.endDate.format(
                                                dayMonthYearFormatter
                                            )
                                        }",
                                onClick = {
                                    if (schedulesWithEvents.size > 1) {
                                        scheduleViewModel.setDefaultSchedule(
                                            scheduleWithEvents.schedule.id,
                                            scheduleWithEvents.schedule.timetableId
                                        )
                                    }
                                },
                                trailingIcon = if (schedulesWithEvents.size > 1) {
                                    {
                                        RadioButton(
                                            selected = (scheduleWithEvents.schedule.id == currentSchedule?.id && isSavedNamedSchedule)
                                                    || scheduleWithEvents.schedule.isDefault,
                                            onClick = {
                                                scheduleViewModel.setDefaultSchedule(
                                                    scheduleWithEvents.schedule.id,
                                                    scheduleWithEvents.schedule.timetableId
                                                )
                                            }
                                        )
                                    }
                                } else null,
                                showClickLabel = false,
                                verticalPadding = 8.dp
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ModalDialogNamedScheduleHeader(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,
    namedSchedule: NamedSchedule,
    isSavedNamedSchedule: Boolean,
    isDefaultNamedSchedule: Boolean,
    onDismiss: (NamedSchedule?) -> Unit
) {
    val locale = LocalLocale.current.platformLocale
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = namedSchedule.shortName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            if (namedSchedule.type != NamedScheduleType.MY && isSavedNamedSchedule) {
                val lastTimeUpdate = remember(
                    namedSchedule.lastTimeUpdate
                ) {
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(namedSchedule.lastTimeUpdate),
                        ZoneId.systemDefault()
                    ).format(dayMonthNameFormatter.withLocale(locale))

                }

                Text(
                    text = "${stringResource(R.string.current_on)} $lastTimeUpdate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

            }
        }
        if (isSavedNamedSchedule) {
            LargeIconButton(
                onClick = {
                    onDismiss(null)
                    appBackStack.openDialog(
                        Route.Dialog.RenameNamedScheduleDialog(
                            namedSchedule.id,
                            namedSchedule.fullName
                        )
                    )
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                icon = ImageVector.vectorResource(R.drawable.edit),
                contentDescription = stringResource(R.string.rename)
            )
        }
        if (isSavedNamedSchedule) {
            LargeIconButton(
                onClick = {
                    showDeleteDialog = true
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                icon = ImageVector.vectorResource(R.drawable.delete),
                contentDescription = stringResource(R.string.delete)
            )
        }
    }

    if (showDeleteDialog) {
        CustomAlertDialog(
            dialogTitle = stringResource(R.string.delete_schedule),
            dialogText = stringResource(R.string.do_you_want_continue),
            onDismissRequest = {
                showDeleteDialog = false
            },
            onConfirmation = {
                scheduleViewModel.deleteNamedSchedule(
                    namedSchedule.id,
                    isDefaultNamedSchedule
                )
                onDismiss(null)
            }
        )
    }
}

@Composable
fun LargeIconButton(
    onClick: () -> Unit,
    colors: IconButtonColors,
    icon: ImageVector,
    contentDescription: String
) {
    IconButton(
        modifier = Modifier.size(48.dp),
        onClick = onClick,
        colors = colors
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}