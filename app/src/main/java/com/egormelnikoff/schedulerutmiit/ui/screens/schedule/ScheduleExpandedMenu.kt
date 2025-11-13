package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import java.time.format.DateTimeFormatter

@Composable
fun ExpandedMenu(
    setDefaultSchedule: (Triple<Long, Long, String>) -> Unit,
    scheduleState: ScheduleState,
    expandedSchedulesMenu: Boolean,
    onShowExpandedMenu: (Boolean) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    AnimatedVisibility(
        visible = expandedSchedulesMenu,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            scheduleState.currentNamedScheduleData!!.namedSchedule!!.schedules.forEach { schedule ->
                val scale by animateFloatAsState(
                    targetValue = if (schedule.scheduleEntity.isDefault) 1f else 0f
                )
                ClickableItem(
                    title = schedule.scheduleEntity.typeName,
                    subtitle = "${schedule.scheduleEntity.startDate.format(formatter)} - ${
                        schedule.scheduleEntity.endDate.format(
                            formatter
                        )
                    }",
                    verticalPadding = 4.dp,
                    onClick = {
                        setDefaultSchedule(
                            Triple(
                                scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.id,
                                schedule.scheduleEntity.id,
                                schedule.scheduleEntity.timetableId
                            )
                        )
                    },
                    showClickLabel = false,
                    trailingIcon = {
                        Icon(
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer(scaleX = scale, scaleY = scale),
                            imageVector = ImageVector.vectorResource(R.drawable.check),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
            ClickableItem(
                verticalPadding = 4.dp,
                title = LocalContext.current.getString(R.string.collapse),
                titleColor = MaterialTheme.colorScheme.onSecondaryContainer,
                titleTypography = MaterialTheme.typography.titleSmall,
                onClick = {
                    onShowExpandedMenu(false)
                },
                showClickLabel = false,
                trailingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.up),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            )
        }
    }
}