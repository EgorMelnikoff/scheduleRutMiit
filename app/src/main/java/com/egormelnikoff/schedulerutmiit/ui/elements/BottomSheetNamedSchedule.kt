package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.ScheduleActions
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogNamedSchedule(
    namedScheduleEntity: NamedScheduleEntity,
    appUiState: AppUiState? = null,
    scheduleActions: ScheduleActions? = null,
    namedScheduleData: NamedScheduleData? = null,
    navigateToRenameDialog: (() -> Unit)? = null,
    navigateToHiddenEvents: ((ScheduleEntity) -> Unit)? = null,
    onOpenNamedSchedule: (() -> Unit)? = null,
    onSetDefaultNamedSchedule: (() -> Unit)? = null,
    onDeleteNamedSchedule: (() -> Unit)? = null,
    onLoadInitialData: (() -> Unit)? = null,
    onSaveCurrentNamedSchedule: (() -> Unit)? = null,
    onDismiss: (NamedScheduleEntity?) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    CustomModalBottomSheet(
        modifier = Modifier.padding(horizontal = 12.dp),
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
                    text = namedScheduleEntity.shortName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
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
                    contentDescription = stringResource(R.string.rename)
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
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
        Spacer(modifier = Modifier.height(0.dp))
        if (scheduleActions != null && namedScheduleData?.namedSchedule?.schedules != null) {
            ColumnGroup(
                items = namedScheduleData.namedSchedule.schedules.map { schedule ->
                    {
                        val scale by animateFloatAsState(
                            targetValue = if (schedule.scheduleEntity.isDefault) 1f else 0f
                        )
                        ClickableItem(
                            title = schedule.scheduleEntity.timetableType.typeName,
                            titleLabel = {
                                Icon(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .graphicsLayer(scaleX = scale, scaleY = scale),
                                    imageVector = ImageVector.vectorResource(R.drawable.check),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                            },
                            subtitle = "${schedule.scheduleEntity.startDate.format(formatter)} - ${
                                schedule.scheduleEntity.endDate.format(formatter)
                            }",
                            verticalPadding = 8.dp,
                            trailingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (!schedule.scheduleEntity.isDefault) {
                                        IconButton(
                                            onClick = {
                                                scheduleActions.onSetDefaultSchedule(
                                                    schedule.scheduleEntity.id,
                                                    schedule.scheduleEntity.timetableId
                                                )
                                            },
                                            colors = IconButtonDefaults.iconButtonColors().copy(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onBackground
                                            )
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(24.dp),
                                                imageVector = ImageVector.vectorResource(R.drawable.check),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                    if (appUiState != null && schedule.scheduleEntity.downloadUrl != null) {
                                        IconButton(
                                            onClick = {
                                                appUiState.uriHandler.openUri(
                                                    schedule.scheduleEntity.downloadUrl
                                                )
                                            },
                                            colors = IconButtonDefaults.iconButtonColors().copy(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onBackground
                                            )
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(24.dp),
                                                imageVector = ImageVector.vectorResource(R.drawable.download),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                    if (namedScheduleData.scheduleData?.schedulePagerData!!.today > schedule.scheduleEntity.endDate) {
                                        IconButton(
                                            onClick = {
                                                scheduleActions.onDeleteSchedule(
                                                    namedScheduleEntity.id,
                                                    schedule.scheduleEntity.id
                                                )
                                            },
                                            colors = IconButtonDefaults.iconButtonColors().copy(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(24.dp),
                                                imageVector = ImageVector.vectorResource(R.drawable.delete),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(0.dp))
        }
        ColumnGroup(
            items = buildList {
                if (navigateToHiddenEvents != null && namedScheduleData?.scheduleData?.scheduleEntity != null) {
                    add {
                        ActionDialogButton(
                            icon = ImageVector.vectorResource(R.drawable.visibility_off),
                            title = stringResource(R.string.hidden_events),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            navigateToHiddenEvents(namedScheduleData.scheduleData.scheduleEntity)
                            onDismiss(null)
                        }
                    }
                }
                onSaveCurrentNamedSchedule?.let {
                    add {
                        ActionDialogButton(
                            icon = ImageVector.vectorResource(R.drawable.save),
                            title = stringResource(R.string.save),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            onSaveCurrentNamedSchedule()
                            onDismiss(null)
                        }
                    }
                }
                onOpenNamedSchedule?.let {
                    add {
                        ActionDialogButton(
                            icon = ImageVector.vectorResource(R.drawable.open),
                            title = stringResource(R.string.open),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            onOpenNamedSchedule()
                            onDismiss(null)
                        }
                    }
                }
                onSetDefaultNamedSchedule?.let {
                    add {
                        ActionDialogButton(
                            icon = ImageVector.vectorResource(R.drawable.check),
                            title = stringResource(R.string.make_default),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            onSetDefaultNamedSchedule()
                            onDismiss(null)
                        }
                    }
                }
                onLoadInitialData?.let {
                    add {
                        ActionDialogButton(
                            icon = ImageVector.vectorResource(R.drawable.back),
                            title = stringResource(R.string.return_default),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            onLoadInitialData()
                            onDismiss(null)
                        }
                    }
                }
            }
        )
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