package com.egormelnikoff.schedulerutmiit.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.egormelnikoff.schedulerutmiit.ui.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.composable.GroupItem
import com.egormelnikoff.schedulerutmiit.ui.composable.SimpleTopBar
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun SchedulesDialog(
    onBack: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSchedule: () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    scheduleUiState: ScheduleUiState,
    paddingValues: PaddingValues
) {
    var namedScheduleActionsDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    Scaffold(
        topBar = {
            SimpleTopBar(
                title = LocalContext.current.getString(R.string.schedules),
                navAction = { onBack() },
                navImageVector = ImageVector.vectorResource(R.drawable.back)
            ) {
                IconButton(
                    onClick = {
                        navigateToSearch()
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                }
            }
        }
    ) { padding ->
        if (scheduleUiState.savedNamedSchedules.isNotEmpty()) {
            val groupedSchedules by remember(
                scheduleUiState.savedNamedSchedules
            ) {
                mutableStateOf(
                    scheduleUiState.savedNamedSchedules
                        .sortedBy { it.type }
                        .groupBy { it.type }
                        .toList()
                )
            }
            val defaultNamedSchedule = scheduleUiState.savedNamedSchedules.find { it.isDefault }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    )
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (defaultNamedSchedule != null) {
                    DefaultNamedScheduleItem(
                        namedScheduleEntity = defaultNamedSchedule
                    )
                }
                groupedSchedules.forEachIndexed { index, namedSchedules ->
                    GroupItem(
                        title = when (namedSchedules.first) {
                            0 -> LocalContext.current.getString(R.string.Groups)
                            1 -> LocalContext.current.getString(R.string.Lecturers)
                            2 -> LocalContext.current.getString(R.string.Rooms)
                            3 -> LocalContext.current.getString(R.string.my)
                            else -> LocalContext.current.getString(R.string.schedules)
                        },
                        items = namedSchedules.second.map { namedScheduleEntity ->
                            {
                                NamedScheduleItem(
                                    namedScheduleEntity = namedScheduleEntity,
                                    onShowActionsDialog = {
                                        namedScheduleActionsDialog = namedScheduleEntity
                                    }
                                )
                            }
                        }
                    )
                    if (index == groupedSchedules.lastIndex) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        } else {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                buttonTitle = LocalContext.current.getString(R.string.search),
                imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                action = { navigateToSearch() },
                paddingBottom = 0.dp
            )
        }
        if (namedScheduleActionsDialog != null) {
            DialogNamedScheduleActions(
                title = namedScheduleActionsDialog!!.shortName,
                isDefault = namedScheduleActionsDialog!!.isDefault,
                onSet = {
                    if (namedScheduleActionsDialog!!.id != scheduleUiState.currentNamedSchedule?.namedScheduleEntity?.id) {
                        scheduleViewModel.getNamedScheduleFromDb(
                            primaryKeyNamedSchedule = namedScheduleActionsDialog!!.id
                        )
                    }
                    navigateToSchedule()
                    namedScheduleActionsDialog = null
                },
                onSelectDefault = {
                    scheduleViewModel.getNamedScheduleFromDb(
                        primaryKeyNamedSchedule = namedScheduleActionsDialog!!.id,
                        setDefault = true
                    )
                    navigateToSchedule()
                    namedScheduleActionsDialog = null
                },
                onDelete = {
                    scheduleViewModel.deleteNamedSchedule(
                        primaryKeyNamedSchedule = namedScheduleActionsDialog!!.id,
                        isDefault = true
                    )
                    namedScheduleActionsDialog = null
                },
                onDismiss = {
                    namedScheduleActionsDialog = null
                }
            )
        }
    }
}

@Composable
fun DefaultNamedScheduleItem(
    namedScheduleEntity: NamedScheduleEntity
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = ImageVector.vectorResource(R.drawable.check),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = LocalContext.current.getString(R.string.default_schedule),
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = namedScheduleEntity.shortName,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun NamedScheduleItem(
    namedScheduleEntity: NamedScheduleEntity,
    onShowActionsDialog: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
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
        OutlinedIconButton(
            onClick = {
                onShowActionsDialog()
            },
            colors = IconButtonDefaults.outlinedIconButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.more_vert),
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNamedScheduleActions(
    title: String,
    isDefault: Boolean,
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
                text = title,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (!isDefault) {
                DialogNamedScheduleAction(
                    onClick = {
                        onSelectDefault()
                    },
                    icon = ImageVector.vectorResource(R.drawable.check),
                    title = LocalContext.current.getString(R.string.make_default),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            }
            DialogNamedScheduleAction(
                onClick = {
                    onSet()
                },
                icon = ImageVector.vectorResource(R.drawable.open),
                title = LocalContext.current.getString(R.string.open),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
            DialogNamedScheduleAction(
                onClick = {
                    onDelete()
                },
                icon = ImageVector.vectorResource(R.drawable.open),
                title = LocalContext.current.getString(R.string.delete),
                contentColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DialogNamedScheduleAction(
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