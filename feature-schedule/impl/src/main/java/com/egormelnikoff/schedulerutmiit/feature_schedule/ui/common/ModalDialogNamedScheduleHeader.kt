package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButtonDefaults
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
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppAlertBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
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
        modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
        ) {
            Text(
                text = namedSchedule.shortName,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.onBackground,
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
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSecondaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                isSavedNamedSchedule && schedule != null -> {
                    Text(
                        text = schedule.timetableType.typeName,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSecondaryContainer,
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
                    containerColor = AppTheme.colors.secondaryContainer,
                    contentColor = AppTheme.colors.onBackground
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
                    containerColor = AppTheme.colors.error,
                    contentColor = AppTheme.colors.onPrimary
                ),
                icon = ImageVector.vectorResource(R.drawable.delete),
                contentDescription = stringResource(R.string.delete)
            )
        }
    }

    if (showDeleteDialog) {
        AppAlertBottomSheet(
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
