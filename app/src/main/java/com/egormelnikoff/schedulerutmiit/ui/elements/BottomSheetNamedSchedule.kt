package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogNamedSchedule(
    namedScheduleEntity: NamedScheduleEntity,
    currentScheduleEntity: ScheduleEntity? = null,
    schedules: List<ScheduleFormatted>? = null,

    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,
    isSavedNamedSchedule: Boolean,
    isDefaultNamedSchedule: Boolean,
    haveNotEmptySchedules: Boolean = false,
    haveHiddenEvents: Boolean = false,

    onOpenNamedSchedule: (() -> Unit)? = null,
    onDismiss: (NamedScheduleEntity?) -> Unit
) {
    CustomModalBottomSheet(
        onDismiss = {
            onDismiss(null)
        }
    ) {
        ModalDialogNamedScheduleHeader(
            appBackStack = appBackStack,
            scheduleViewModel = scheduleViewModel,
            namedScheduleEntity = namedScheduleEntity,
            isSavedNamedSchedule = isSavedNamedSchedule,
            isDefaultNamedSchedule = isDefaultNamedSchedule,
            onDismiss = onDismiss
        )
        schedules?.let {
            Spacer(modifier = Modifier.height(4.dp))
            ColumnGroup(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = schedules.map { schedule ->
                    {
                        var showScheduleDialog by remember { mutableStateOf(false) }
                        val angle by animateFloatAsState(
                            targetValue = if (showScheduleDialog) 0f else 180f
                        )
                        Column {
                            ClickableItem(
                                title = schedule.scheduleEntity.timetableType.typeName,
                                titleLabel = if (schedule.scheduleEntity.id == currentScheduleEntity?.id && isSavedNamedSchedule) {
                                    {
                                        Icon(
                                            modifier = Modifier.size(16.dp),
                                            imageVector = ImageVector.vectorResource(R.drawable.check),
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = null
                                        )
                                    }
                                } else null,
                                subtitle = "${schedule.scheduleEntity.startDate.format(dayMonthYearFormatter)} - " +
                                        "${schedule.scheduleEntity.endDate.format(dayMonthYearFormatter)}",
                                onClick = {
                                    showScheduleDialog = !showScheduleDialog
                                },
                                trailingIcon = {
                                    Icon(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer(
                                                rotationZ = angle
                                            ),
                                        imageVector = ImageVector.vectorResource(R.drawable.up),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                },
                                showClickLabel = false,
                                verticalPadding = 8.dp
                            )
                            ScheduleActionsDialog(
                                showExpandedMenu = showScheduleDialog,
                                namedScheduleEntity = namedScheduleEntity,
                                schedule = schedule,

                                scheduleViewModel = scheduleViewModel,
                                appBackStack = appBackStack,

                                isSavedNamedSchedule = isSavedNamedSchedule,
                                isDefaultSchedule = isSavedNamedSchedule && schedule.scheduleEntity.id == currentScheduleEntity?.id || schedule.scheduleEntity.isDefault,
                                haveDownloadUrl = schedule.scheduleEntity.downloadUrl != null,
                                haveNotEmptySchedules = haveNotEmptySchedules,
                                haveHiddenEvents = haveHiddenEvents,

                                onDismissParentDialog = onDismiss
                            )
                        }
                    }
                }
            )
        }
        onOpenNamedSchedule?.let {
            Spacer(modifier = Modifier.height(4.dp))
            ActionDialogButton(
                icon = ImageVector.vectorResource(R.drawable.open_panel),
                title = stringResource(R.string.open),
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                onOpenNamedSchedule()
                onDismiss(null)
            }
        }
    }
}

@Composable
fun ModalDialogNamedScheduleHeader(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,
    namedScheduleEntity: NamedScheduleEntity,
    isSavedNamedSchedule: Boolean,
    isDefaultNamedSchedule: Boolean,
    onDismiss: (NamedScheduleEntity?) -> Unit
) {
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
                text = namedScheduleEntity.shortName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            if (namedScheduleEntity.type != NamedScheduleType.MY && isSavedNamedSchedule) {
                val lastTimeUpdate = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(namedScheduleEntity.lastTimeUpdate),
                    ZoneId.systemDefault()
                ).format(dayMonthNameFormatter)

                Text(
                    text = "${stringResource(R.string.current_on)} $lastTimeUpdate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

            }
        }
        if (isSavedNamedSchedule && !isDefaultNamedSchedule) {
            LargeIconButton(
                onClick = {
                    scheduleViewModel.getSavedNamedSchedule(
                        namedScheduleId = namedScheduleEntity.id,
                        setDefault = true
                    )
                    onDismiss(null)
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                icon = ImageVector.vectorResource(R.drawable.check),
                contentDescription = stringResource(R.string.make_default)
            )
        }
        if (isSavedNamedSchedule) {
            LargeIconButton(
                onClick = {
                    appBackStack.openDialog(
                        Route.Dialog.RenameNamedScheduleDialog(
                            namedScheduleEntity
                        )
                    )
                    onDismiss(null)
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
                    namedScheduleEntity.id,
                    isDefaultNamedSchedule
                )
                onDismiss(null)
            }
        )
    }
}


@Composable
fun ScheduleActionsDialog(
    showExpandedMenu: Boolean,
    namedScheduleEntity: NamedScheduleEntity,
    schedule: ScheduleFormatted,

    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,
    isSavedNamedSchedule: Boolean,
    isDefaultSchedule: Boolean,
    haveDownloadUrl: Boolean,
    haveNotEmptySchedules: Boolean,
    haveHiddenEvents: Boolean,

    onDismissParentDialog: (NamedScheduleEntity?) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    AnimatedVisibility(
        visible = showExpandedMenu
    ) {
        Column {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline
            )
            if (haveDownloadUrl) {
                ClickableItem(
                    title = "${stringResource(R.string.download)} ${stringResource(R.string.schedule).replaceFirstChar { it.lowercase() }}",
                    titleTypography = MaterialTheme.typography.titleMedium,
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.download),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.pdf),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    showClickLabel = false
                ) {
                    schedule.scheduleEntity.downloadUrl?.let {
                        uriHandler.openUri(it)
                    }
                }
            }
            if (isSavedNamedSchedule && haveNotEmptySchedules) {
                ClickableItem(
                    title = stringResource(R.string.add_class),
                    titleTypography = MaterialTheme.typography.titleMedium,
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    showClickLabel = false
                ) {
                    appBackStack.openDialog(
                        Route.Dialog.AddEventDialog(
                            namedScheduleEntity,
                            schedule.scheduleEntity
                        )
                    )
                    onDismissParentDialog(null)
                }
            }
            if (isSavedNamedSchedule && haveHiddenEvents) {
                ClickableItem(
                    title = stringResource(R.string.hidden_events),
                    titleTypography = MaterialTheme.typography.titleMedium,
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.visibility_off),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    showClickLabel = false
                ) {
                    appBackStack.openDialog(
                        Route.Dialog.HiddenEventsDialog(
                            namedScheduleEntity,
                            schedule.scheduleEntity
                        )
                    )
                    onDismissParentDialog(null)
                }
            }
            if (!isDefaultSchedule) {
                ClickableItem(
                    title = "${stringResource(R.string.open)} ${stringResource(R.string.schedule).replaceFirstChar { it.lowercase() }}",
                    titleTypography = MaterialTheme.typography.titleMedium,
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.check),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    showClickLabel = false
                ) {
                    scheduleViewModel.setDefaultSchedule(
                        schedule.scheduleEntity.id,
                        schedule.scheduleEntity.timetableId
                    )
                }
            }
            if (namedScheduleEntity.type != NamedScheduleType.MY && isSavedNamedSchedule) {
                ClickableItem(
                    title = stringResource(R.string.delete),
                    titleTypography = MaterialTheme.typography.titleMedium,
                    titleColor = MaterialTheme.colorScheme.error,
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.delete),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    showClickLabel = false
                ) {
                    showDeleteDialog = true
                    scheduleViewModel.deleteSchedule(
                        namedScheduleEntity.id,
                        schedule.scheduleEntity.id
                    )
                }
            }
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
                scheduleViewModel.deleteSchedule(
                    namedScheduleEntity.id,
                    schedule.scheduleEntity.id
                )
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