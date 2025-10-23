package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState
import java.time.format.DateTimeFormatter

@Composable
fun ExpandedMenu(
    setDefaultSchedule: (Triple<Long, Long, String>) -> Unit,
    scheduleUiState: ScheduleUiState,
    expandedSchedulesMenu: Boolean,
    onShowExpandedMenu: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = expandedSchedulesMenu,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp
            )
            scheduleUiState.currentScheduleData!!.namedSchedule!!.schedules.forEach { schedule ->
                val scale by animateFloatAsState(
                    targetValue = if (schedule.scheduleEntity.isDefault) 1f else 0f
                )
                ExpandedMenuItem(
                    onClick = {
                        setDefaultSchedule(
                            Triple(
                                scheduleUiState.currentScheduleData.namedSchedule.namedScheduleEntity.id,
                                schedule.scheduleEntity.id,
                                schedule.scheduleEntity.timetableId
                            )
                        )
                    },
                    title = {
                        Column {
                            Text(
                                text = schedule.scheduleEntity.typeName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = schedule.scheduleEntity.startDate.format(
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                    ),
                                    maxLines = 1,
                                    fontSize = 12.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    modifier = Modifier.size(12.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.forward),
                                    contentDescription = null
                                )
                                Text(
                                    text = schedule.scheduleEntity.endDate.format(
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                    ),
                                    maxLines = 1,
                                    fontSize = 12.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    scale = scale,
                    trailingIcon = ImageVector.vectorResource(R.drawable.check),
                    verticalPadding = 4,
                    trailingIconColor = MaterialTheme.colorScheme.primary
                )
            }
            ExpandedMenuItem(
                onClick = {
                    onShowExpandedMenu(false)
                },
                title = {
                    Text(
                        text = LocalContext.current.getString(R.string.collapse),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingIcon = ImageVector.vectorResource(R.drawable.up),
                verticalPadding = 4
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun ExpandedMenuItem(
    onClick: () -> Unit,
    leadingIcon: ImageVector? = null,
    title: @Composable (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    scale: Float? = null,
    verticalPadding: Int,
    trailingIconColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = verticalPadding.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        leadingIcon?.let {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier.weight(1f)
        ) {
            title?.invoke()
        }
        trailingIcon?.let {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .let {
                        if (scale != null)
                            it.graphicsLayer(scaleX = scale, scaleY = scale)
                        else it
                    },
                imageVector = trailingIcon,
                contentDescription = null,
                tint = trailingIconColor ?: MaterialTheme.colorScheme.onSurface
            )
        }
    }
}