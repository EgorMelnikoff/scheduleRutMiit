package com.egormelnikoff.schedulerutmiit.ui.elements

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(
    title: String,
    navAction: (() -> Unit)? = null,
    navImageVector: ImageVector,
    actions: @Composable (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        navigationIcon = {
            if (navAction != null) {
                IconButton(
                    onClick = navAction
                ) {
                    Icon(
                        imageVector = navImageVector,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            actions?.invoke()
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTopBar(
    scheduleUiState: ScheduleUiState,
    scheduleViewModel: ScheduleViewModel,

    navigateToAddEvent: (ScheduleEntity) -> Unit,

    onShowExpandedMenu: (Boolean) -> Unit,
    expandedSchedulesMenu: Boolean,

    onLoadDefaultSchedule: () -> Unit,
    onShowLoadDefaultScheduleButton: Boolean,
    preferencesDataStore: DataStore,
    isScheduleCalendar: Boolean
) {
    val isNotEmpty = scheduleUiState.currentNamedSchedule!!.schedules.isNotEmpty() && scheduleUiState.currentScheduleEntity != null
    val isSomeSchedules = scheduleUiState.currentNamedSchedule.schedules.size > 1
    val isCustomSchedule = scheduleUiState.currentNamedSchedule.namedScheduleEntity.type == 3

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val rotationAngle by animateFloatAsState(
        targetValue = if (expandedSchedulesMenu) 180f else 0f
    )


    TopAppBar(
        navigationIcon = {
            if (onShowLoadDefaultScheduleButton) {
                IconButton(
                    onClick = {
                        onLoadDefaultSchedule()
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.back),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .let {
                        if (isSomeSchedules) {
                            it
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    onClick = {
                                        onShowExpandedMenu(!expandedSchedulesMenu)
                                    }
                                )
                        } else {
                            it
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column {
                    Text(
                        text = scheduleUiState.currentNamedSchedule.namedScheduleEntity.shortName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (scheduleUiState.currentScheduleEntity != null && !isCustomSchedule) {
                        Text(
                            text = scheduleUiState.currentScheduleEntity.typeName,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (isSomeSchedules) {
                    Icon(
                        modifier = Modifier.graphicsLayer(
                            rotationZ = rotationAngle
                        ),
                        imageVector = ImageVector.vectorResource(R.drawable.down),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            if (isNotEmpty && !isCustomSchedule) {
                IconButton(
                    onClick = {
                        val url = scheduleUiState.currentScheduleEntity?.downloadUrl
                        val intent = Intent(Intent.ACTION_VIEW, url?.toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.download),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            if (isNotEmpty && isCustomSchedule) {
                IconButton(
                    onClick = {
                        navigateToAddEvent(scheduleUiState.currentScheduleEntity!!)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            IconButton(
                onClick = {
                    scope.launch {
                        if (isScheduleCalendar) {
                            preferencesDataStore.setScheduleView(false)
                        } else {
                            preferencesDataStore.setScheduleView(true)
                        }
                    }

                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (isScheduleCalendar) ImageVector.vectorResource(R.drawable.list) else ImageVector.vectorResource(
                        R.drawable.calendar
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconToggleButton(
                checked = scheduleUiState.isSaved,
                enabled = if (scheduleUiState.isSaved) true else scheduleUiState.isSavingAvailable,
                colors = IconToggleButtonColors(
                    containerColor = Color.Unspecified,
                    checkedContainerColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,

                    contentColor = MaterialTheme.colorScheme.onBackground,
                    checkedContentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                ),
                onCheckedChange = {
                    if (scheduleUiState.isSaved) {
                        scheduleViewModel.deleteNamedSchedule(
                            primaryKeyNamedSchedule = scheduleUiState.currentNamedSchedule.namedScheduleEntity.id,
                            isDefault = scheduleUiState.currentNamedSchedule.namedScheduleEntity.isDefault
                        )
                    } else {
                        scheduleViewModel.saveCurrentNamedSchedule()
                    }
                }
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = if (scheduleUiState.isSaved) ImageVector.vectorResource(R.drawable.save_fill) else ImageVector.vectorResource(
                        R.drawable.save
                    ),
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        )
    )
}