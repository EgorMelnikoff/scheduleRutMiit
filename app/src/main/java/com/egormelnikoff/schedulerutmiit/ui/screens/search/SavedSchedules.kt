package com.egormelnikoff.schedulerutmiit.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SavedSchedules(
    scheduleUiState: ScheduleUiState,
    namedScheduleActionsDialog: NamedScheduleEntity?,
    onShowActionsDialog: (NamedScheduleEntity?) -> Unit,
    scheduleViewModel: ScheduleViewModel,
    navigateToSchedule: () -> Unit
) {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
    ) {
        ColumnGroup(
            title = LocalContext.current.getString(R.string.saved_schedules),
            items = scheduleUiState.savedNamedSchedules.map { namedScheduleEntity ->
                {
                    NamedScheduleItem(
                        namedScheduleEntity = namedScheduleEntity,
                        onShowActionsDialog = {
                            onShowActionsDialog(namedScheduleEntity)
                        }
                    )
                }
            }
        )
    }
    if (namedScheduleActionsDialog != null) {
        DialogNamedScheduleActions(
            namedScheduleEntity = namedScheduleActionsDialog,
            onSet = {
                if (namedScheduleActionsDialog.id != scheduleUiState.currentNamedSchedule?.namedScheduleEntity?.id) {
                    scheduleViewModel.getNamedScheduleFromDb(
                        primaryKeyNamedSchedule = namedScheduleActionsDialog.id
                    )
                }
                navigateToSchedule()
                onShowActionsDialog(null)
            },
            onSelectDefault = {
                scheduleViewModel.getNamedScheduleFromDb(
                    primaryKeyNamedSchedule = namedScheduleActionsDialog.id,
                    setDefault = true
                )
                navigateToSchedule()
                onShowActionsDialog(null)
            },
            onDelete = {
                scheduleViewModel.deleteNamedSchedule(
                    primaryKeyNamedSchedule = namedScheduleActionsDialog.id,
                    isDefault = true
                )
                onShowActionsDialog(null)
            },
            onDismiss = {
                onShowActionsDialog(null)
            }
        )
    }
}

@Composable
fun NamedScheduleItem(
    namedScheduleEntity: NamedScheduleEntity,
    onShowActionsDialog: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onShowActionsDialog()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .defaultMinSize(minHeight = 52.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val instant = Instant.ofEpochMilli(namedScheduleEntity.lastTimeUpdate)
            val formatter = DateTimeFormatter.ofPattern("d MMM, HH:mm", Locale.getDefault())
            val lastTimeUpdate =
                formatter.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
            Text(
                text = namedScheduleEntity.shortName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            if (namedScheduleEntity.type != 3) {
                Text(
                    text = "${LocalContext.current.getString(R.string.Current_on)} $lastTimeUpdate",
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1, style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (namedScheduleEntity.isDefault) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNamedScheduleActions(
    namedScheduleEntity: NamedScheduleEntity,
    onSet: () -> Unit,
    onSelectDefault: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: (NamedScheduleEntity?) -> Unit
) {
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = {
            onDismiss(null)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = namedScheduleEntity.fullName,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (!namedScheduleEntity.isDefault) {
                DialogNamedScheduleButton(
                    onClick = {
                        onSelectDefault()
                    },
                    icon = ImageVector.vectorResource(R.drawable.check),
                    title = LocalContext.current.getString(R.string.make_default),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            }
            DialogNamedScheduleButton(
                onClick = {
                    onSet()
                },
                icon = ImageVector.vectorResource(R.drawable.open),
                title = LocalContext.current.getString(R.string.open),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
            DialogNamedScheduleButton(
                onClick = {
                    onDelete()
                },
                icon = ImageVector.vectorResource(R.drawable.delete),
                title = LocalContext.current.getString(R.string.delete),
                contentColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DialogNamedScheduleButton(
    onClick: () -> Unit,
    icon: ImageVector,
    title: String,
    contentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
        Text(
            text = title,
            fontSize = 14.sp,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}