package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog.AppAlertDialog
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Schedule
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun ModalDialogNamedScheduleHeader(
    namedSchedule: NamedSchedule,
    schedule: Schedule?,
    isSavedNamedSchedule: Boolean,

    onOpenRenameDialog: () -> Unit,
    onDeleteNamedSchedule: () -> Unit,
    onDismiss: () -> Unit
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

            when {
                isSavedNamedSchedule && namedSchedule.type != NamedScheduleType.MY -> {
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

                isSavedNamedSchedule && schedule != null -> {
                    Text(
                        text = schedule.timetableType.typeName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
        }
        if (isSavedNamedSchedule) {
            LargeIconButton(
                onClick = {
                    onDismiss()
                    onOpenRenameDialog()
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
        AppAlertDialog(
            dialogTitle = stringResource(R.string.delete_schedule),
            dialogText = stringResource(R.string.do_you_want_continue),
            onDismissRequest = {
                showDeleteDialog = false
            },
            onConfirmation = {
                onDismiss()
                onDeleteNamedSchedule()
            }
        )
    }
}
