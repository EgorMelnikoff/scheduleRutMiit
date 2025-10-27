package com.egormelnikoff.schedulerutmiit.ui.elements

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    titleContent: @Composable (() -> Unit)? = null,
    titleText: String? = null,
    navAction: (() -> Unit)? = null,
    navImageVector: ImageVector = ImageVector.vectorResource(R.drawable.back),
    actions: @Composable (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            titleText?.let {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            } ?: titleContent?.let {
                titleContent.invoke()
            }
        },
        navigationIcon = {
            navAction?.let {
                IconButton(
                    onClick = it
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

@Composable
fun ScheduleTopAppBar(
    navigateToAddEvent: (ScheduleEntity) -> Unit,
    scheduleUiState: ScheduleUiState,
    calendarView: Boolean,
    context: Context,
    expandedSchedulesMenu: Boolean,
    onShowExpandedMenu: (Boolean) -> Unit,
    onSetScheduleView: (Boolean) -> Unit,
    onShowNamedScheduleDialog: (NamedScheduleEntity) -> Unit
) {
    val isNotEmpty =
        scheduleUiState.currentScheduleData!!.namedSchedule!!.schedules.isNotEmpty() && scheduleUiState.currentScheduleData.settledScheduleEntity != null
    val isSomeSchedules = scheduleUiState.currentScheduleData.namedSchedule.schedules.size > 1
    val isCustomSchedule =
        scheduleUiState.currentScheduleData.namedSchedule.namedScheduleEntity.type == 3
    val rotationAngle by animateFloatAsState(
        targetValue = if (expandedSchedulesMenu) 180f else 0f
    )

    CustomTopAppBar(
        titleContent = {
            Row(
                modifier = if (isSomeSchedules) {
                    Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            onClick = {
                                onShowExpandedMenu(!expandedSchedulesMenu)
                            }
                        )
                } else {
                    Modifier
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Text(
                        text = scheduleUiState.currentScheduleData.namedSchedule.namedScheduleEntity.shortName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    if (scheduleUiState.currentScheduleData.settledScheduleEntity != null && !isCustomSchedule) {
                        Text(
                            text = scheduleUiState.currentScheduleData.settledScheduleEntity.typeName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
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
                        val url =
                            scheduleUiState.currentScheduleData.settledScheduleEntity.downloadUrl
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
                        navigateToAddEvent(scheduleUiState.currentScheduleData.settledScheduleEntity)
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
                    if (calendarView) {
                        onSetScheduleView(false)
                    } else {
                        onSetScheduleView(true)
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (calendarView) ImageVector.vectorResource(R.drawable.list) else ImageVector.vectorResource(
                        R.drawable.calendar
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = {
                    onShowNamedScheduleDialog(
                        scheduleUiState.currentScheduleData.namedSchedule.namedScheduleEntity
                    )
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.more_vert),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}