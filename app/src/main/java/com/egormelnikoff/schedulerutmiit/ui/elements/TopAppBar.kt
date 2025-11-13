package com.egormelnikoff.schedulerutmiit.ui.elements

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState

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
    navigateToAddEvent: () -> Unit,
    onShowExpandedMenu: ((Boolean) -> Unit)?,
    onSetScheduleView: (Boolean) -> Unit,
    onShowNamedScheduleDialog: (NamedScheduleEntity) -> Unit,

    scheduleState: ScheduleState,
    calendarView: Boolean,

    expandedSchedulesMenu: Boolean?,
    context: Context
) {
    val isNotEmpty =
        scheduleState.currentNamedScheduleData!!.namedSchedule!!.schedules.isNotEmpty() && scheduleState.currentNamedScheduleData.settledScheduleEntity != null
    val isSomeSchedules = scheduleState.currentNamedScheduleData.namedSchedule.schedules.size > 1
    val isCustomSchedule =
        scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.type == 3
    val rotationAngle by animateFloatAsState(
        targetValue = if (expandedSchedulesMenu == true) 180f else 0f
    )

    CustomTopAppBar(
        titleContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.shortName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    if (scheduleState.currentNamedScheduleData.settledScheduleEntity != null && !isCustomSchedule) {
                        Text(
                            text = scheduleState.currentNamedScheduleData.settledScheduleEntity.typeName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
                if (isSomeSchedules) {
                    IconButton(
                        onClick = {
                            expandedSchedulesMenu?.let { onShowExpandedMenu?.invoke(!it) }
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .graphicsLayer(
                                    rotationZ = rotationAngle
                                )
                                .size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.down),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        actions = {
            if (isNotEmpty && !isCustomSchedule) {
                IconButton(
                    onClick = {
                        val url =
                            scheduleState.currentNamedScheduleData.settledScheduleEntity.downloadUrl
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
                        navigateToAddEvent()
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
                        scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTopAppBar(
    title: String,
    subtitle: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navAction: (() -> Unit)? = null,
    navImageVector: ImageVector = ImageVector.vectorResource(R.drawable.back),
    actions: @Composable (() -> Unit)? = null,
) {
    MediumTopAppBar(
        expandedHeight = TopAppBarDefaults.MediumAppBarExpandedHeight + 16.dp,
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
        title = {
            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

        },
        actions = {
            actions?.invoke()
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        scrollBehavior = scrollBehavior
    )
}