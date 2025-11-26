package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogNamedSchedule(
    namedScheduleEntity: NamedScheduleEntity,
    scheduleData: ScheduleData? = null,
    navigateToRenameDialog: (() -> Unit)? = null,
    navigateToHiddenEvents: ((ScheduleEntity) -> Unit)? = null,
    onDownloadCurrentSchedule: (() -> Unit)? = null,
    onOpenNamedSchedule: (() -> Unit)? = null,
    onSetDefaultNamedSchedule: (() -> Unit)? = null,
    onDeleteNamedSchedule: (() -> Unit)? = null,
    onLoadInitialData: (() -> Unit)? = null,
    onSaveCurrentNamedSchedule: (() -> Unit)? = null,
    onDismiss: (NamedScheduleEntity?) -> Unit
) {
    CustomModalBottomSheet(
        modifier = Modifier.padding(horizontal = 8.dp),
        onDismiss = {
            onDismiss(null)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = namedScheduleEntity.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                if (scheduleData?.scheduleEntity != null && namedScheduleEntity.type != 3) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = scheduleData.scheduleEntity.typeName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            onDownloadCurrentSchedule?.let {
                NamedScheduleIconButton(
                    onClick = {
                        onDownloadCurrentSchedule()
                    },
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    icon = ImageVector.vectorResource(R.drawable.download),
                    contentDescription = LocalContext.current.getString(R.string.download)
                )
            }
            navigateToRenameDialog?.let {
                NamedScheduleIconButton(
                    onClick = {
                        navigateToRenameDialog()
                        onDismiss(null)
                    },
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    icon = ImageVector.vectorResource(R.drawable.edit),
                    contentDescription = LocalContext.current.getString(R.string.rename)
                )
            }
            onDeleteNamedSchedule?.let {
                NamedScheduleIconButton(
                    onClick = {
                        onDeleteNamedSchedule()
                        onDismiss(null)
                    },
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    icon = ImageVector.vectorResource(R.drawable.delete),
                    contentDescription = LocalContext.current.getString(R.string.delete)
                )
            }
        }
        Spacer(modifier = Modifier.height(0.dp))
        if (arrayOf(
                navigateToHiddenEvents,
                onSaveCurrentNamedSchedule,
                onSetDefaultNamedSchedule,
                onLoadInitialData,
                onOpenNamedSchedule
            ).any { it != null }
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline
            )
        }
        if (navigateToHiddenEvents != null && scheduleData?.scheduleEntity != null) {
            ActionDialogButton(
                onClick = {
                    navigateToHiddenEvents(scheduleData.scheduleEntity)
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.visibility_off),
                title = LocalContext.current.getString(R.string.hidden_events),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        onSaveCurrentNamedSchedule?.let {
            ActionDialogButton(
                onClick = {
                    onSaveCurrentNamedSchedule()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.save),
                title = LocalContext.current.getString(R.string.save),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        onOpenNamedSchedule?.let {
            ActionDialogButton(
                onClick = {
                    onOpenNamedSchedule()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.open),
                title = LocalContext.current.getString(R.string.open),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        onSetDefaultNamedSchedule?.let {
            ActionDialogButton(
                onClick = {
                    onSetDefaultNamedSchedule()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.check),
                title = LocalContext.current.getString(R.string.make_default),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        onLoadInitialData?.let {
            ActionDialogButton(
                onClick = {
                    onLoadInitialData()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.back),
                title = LocalContext.current.getString(R.string.return_default),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun NamedScheduleIconButton(
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