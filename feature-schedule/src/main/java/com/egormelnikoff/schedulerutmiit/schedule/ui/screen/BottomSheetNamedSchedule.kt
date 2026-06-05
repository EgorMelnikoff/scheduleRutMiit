package com.egormelnikoff.schedulerutmiit.schedule.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class ActionItem(
    val title: String,
    val imageVector: ImageVector,
    val secondImage: ImageVector? = null,
    val onClick: () -> Unit,
    val color: Color? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogNamedSchedule(
    namedSchedule: NamedSchedule,
    currentSchedule: Schedule? = null,
    schedulesWithEvents: List<ScheduleWithEvents>? = null,
    today: LocalDate,

    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,
    isSavedNamedSchedule: Boolean,
    isDefaultNamedSchedule: Boolean,
    haveHiddenEvents: Boolean = false,
    haveNotEmptySchedules: Boolean = false,

    onOpenNamedSchedule: (() -> Unit)? = null,
    onDismiss: (NamedSchedule?) -> Unit
) {
    val limitActions = remember { 1 }
    val uriHandler = LocalUriHandler.current
    var showDeleteDialog by remember { mutableStateOf<ScheduleWithEvents?>(null) }

    CustomModalBottomSheet(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        onDismiss = {
            onDismiss(null)
        }
    ) {
        ModalDialogNamedScheduleHeader(
            appBackStack = appBackStack,
            scheduleViewModel = scheduleViewModel,
            namedSchedule = namedSchedule,
            currentScheduleTimetableType = currentSchedule?.timetableType,
            isSavedNamedSchedule = isSavedNamedSchedule,
            haveHiddenEvents = haveHiddenEvents,
            isDefaultNamedSchedule = isDefaultNamedSchedule,
            onDismiss = onDismiss
        )

        ColumnGroup(
            modifier = Modifier.padding(horizontal = 16.dp),
            items = buildList {
                if (isSavedNamedSchedule && !isDefaultNamedSchedule) {
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
                            scheduleViewModel.setNamedSchedule(
                                namedScheduleId = namedSchedule.id,
                                setDefault = true
                            )
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
                            onOpenNamedSchedule()
                            onDismiss(null)
                        }
                    }
                }
            }
        )

        schedulesWithEvents?.let {
            ColumnGroup(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = schedulesWithEvents.map { scheduleWithEvents ->
                    {
                        var showScheduleDialog by remember { mutableStateOf(false) }
                        val angle by animateFloatAsState(
                            targetValue = if (showScheduleDialog) 0f else 180f
                        )
                        val isDefaultSchedule =
                            (scheduleWithEvents.schedule.id == currentSchedule?.id && isSavedNamedSchedule)
                                    || scheduleWithEvents.schedule.isDefault
                        val actions = buildList {
                            if (scheduleWithEvents.schedule.downloadUrl != null) {
                                add(
                                    ActionItem(
                                        title = "${stringResource(R.string.download)} ${
                                            stringResource(
                                                R.string.schedule
                                            ).replaceFirstChar { it.lowercase() }
                                        }",
                                        imageVector = ImageVector.vectorResource(R.drawable.download),
                                        secondImage = ImageVector.vectorResource(R.drawable.pdf),
                                        onClick = {
                                            scheduleWithEvents.schedule.downloadUrl.let {
                                                uriHandler.openUri(requireNotNull(it))
                                            }
                                        }
                                    )
                                )
                            }

                            if (isSavedNamedSchedule && haveNotEmptySchedules) {
                                add(
                                    ActionItem(
                                        title = stringResource(R.string.add_class),
                                        imageVector = ImageVector.vectorResource(R.drawable.add),
                                        onClick = {
                                            appBackStack.openDialog(
                                                Route.Dialog.AddEditEventDialog(
                                                    namedScheduleId = namedSchedule.id,
                                                    scheduleId = scheduleWithEvents.schedule.id,
                                                    recurrence = scheduleWithEvents.schedule.recurrence,
                                                    scheduleStartDate = scheduleWithEvents.schedule.startDate,
                                                    scheduleEndDate = scheduleWithEvents.schedule.endDate
                                                )
                                            )
                                            onDismiss(null)
                                        }
                                    )
                                )
                            }
                            if (today > scheduleWithEvents.schedule.endDate && namedSchedule.type != NamedScheduleType.MY && isSavedNamedSchedule) {
                                add(
                                    ActionItem(
                                        title = stringResource(R.string.delete),
                                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                                        color = MaterialTheme.colorScheme.error,
                                        onClick = {
                                            showDeleteDialog = null
                                            scheduleViewModel.deleteSchedule(
                                                namedSchedule.id,
                                                scheduleWithEvents.schedule.id
                                            )
                                        }
                                    )
                                )
                            }
                        }
                        Column {
                            ClickableItem(
                                defaultMinHeight = 40.dp,
                                title = scheduleWithEvents.schedule.timetableType.typeName,
                                titleLabel = if (isDefaultSchedule) {
                                    {
                                        Icon(
                                            modifier = Modifier.size(16.dp),
                                            imageVector = ImageVector.vectorResource(R.drawable.check),
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = null
                                        )
                                    }
                                } else null,
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
                                onClick = if (actions.size > limitActions || showScheduleDialog) {
                                    { showScheduleDialog = !showScheduleDialog }
                                } else null,
                                trailingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        AnimatedVisibility(
                                            visible = !isDefaultSchedule,
                                            enter = fadeIn(),
                                            exit = fadeOut()
                                        ) {
                                            IconButton(
                                                colors = IconButtonDefaults.iconButtonColors()
                                                    .copy(
                                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                        contentColor = MaterialTheme.colorScheme.onBackground
                                                    ),
                                                onClick = {
                                                    scheduleViewModel.setDefaultSchedule(
                                                        scheduleWithEvents.schedule.id,
                                                        scheduleWithEvents.schedule.timetableId
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    modifier = Modifier.size(20.dp),
                                                    imageVector = ImageVector.vectorResource(R.drawable.check),
                                                    contentDescription = null
                                                )
                                            }
                                        }

                                        if (actions.size > limitActions) {
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
                                        } else {
                                            actions.forEach { item ->
                                                IconButton(
                                                    colors = IconButtonDefaults.iconButtonColors()
                                                        .copy(
                                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                            contentColor = MaterialTheme.colorScheme.onBackground
                                                        ),
                                                    onClick = item.onClick
                                                ) {
                                                    Icon(
                                                        modifier = Modifier.size(20.dp),
                                                        imageVector = item.imageVector,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                                showClickLabel = false,
                                verticalPadding = 8.dp
                            )

                            ScheduleActionsDialog(
                                showExpandedMenu = showScheduleDialog,
                                actions = actions
                            )
                        }
                    }
                }
            )
        }

        showDeleteDialog?.let {
            CustomAlertDialog(
                dialogTitle = stringResource(R.string.delete_schedule),
                dialogText = stringResource(R.string.do_you_want_continue),
                onDismissRequest = {
                    showDeleteDialog = null
                },
                onConfirmation = {
                    scheduleViewModel.deleteSchedule(
                        namedSchedule.id,
                        it.schedule.id
                    )
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
    currentScheduleTimetableType: TimetableType?,
    isSavedNamedSchedule: Boolean,
    isDefaultNamedSchedule: Boolean,
    haveHiddenEvents: Boolean,
    onDismiss: (NamedSchedule?) -> Unit
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
                text = namedSchedule.shortName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            if (namedSchedule.type != NamedScheduleType.MY && isSavedNamedSchedule) {
                val lastTimeUpdate = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(namedSchedule.lastTimeUpdate),
                    ZoneId.systemDefault()
                ).format(dayMonthNameFormatter.withLocale(LocalLocale.current.platformLocale))

                Text(
                    text = "${stringResource(R.string.current_on)} $lastTimeUpdate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

            }
        }
        if (haveHiddenEvents) {
            LargeIconButton(
                onClick = {
                    onDismiss(null)
                    appBackStack.openDialog(
                        Route.Dialog.HiddenEventsDialog(
                            namedScheduleId = namedSchedule.id,
                            namedScheduleShortName = namedSchedule.shortName,
                            currentScheduleTimetableType
                        )
                    )
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                icon = ImageVector.vectorResource(R.drawable.visibility_off),
                contentDescription = stringResource(R.string.hidden_events)
            )
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
fun ScheduleActionsDialog(
    showExpandedMenu: Boolean,
    actions: List<ActionItem>
) {

    AnimatedVisibility(
        visible = showExpandedMenu
    ) {
        Column {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline
            )

            actions.forEach { item ->
                ClickableItem(
                    title = item.title,
                    titleColor = item.color ?: MaterialTheme.colorScheme.onBackground,
                    titleTypography = MaterialTheme.typography.titleMedium,
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = item.imageVector,
                            contentDescription = null,
                            tint = item.color ?: MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    },
                    trailingIcon = item.secondImage?.let {
                        {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = item.secondImage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    },
                    showClickLabel = false,
                    onClick = item.onClick
                )
            }
        }
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