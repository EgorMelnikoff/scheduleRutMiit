package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleView
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.next
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    titleContent: @Composable (() -> Unit)? = null,
    titleText: String? = null,
    navAction: (() -> Unit)? = null,
    navImageVector: ImageVector = ImageVector.vectorResource(R.drawable.back),
    actions: @Composable (() -> Unit)? = null
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
    onSetScheduleView: (ScheduleView) -> Unit,
    onShowNamedScheduleDialog: (NamedScheduleEntity) -> Unit,

    namedScheduleData: NamedScheduleData,
    scheduleView: ScheduleView,
    isPeriodic: Boolean,
    isSavedSchedule: Boolean
) {
    val isNotEmpty =
        namedScheduleData.namedSchedule!!.schedules.isNotEmpty() && namedScheduleData.scheduleData?.scheduleEntity != null
    val isCustomSchedule =
        namedScheduleData.namedSchedule.namedScheduleEntity.type == NamedScheduleType.My

    CustomTopAppBar(
        titleContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = namedScheduleData.namedSchedule.namedScheduleEntity.shortName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    if (namedScheduleData.scheduleData?.scheduleEntity != null && !isCustomSchedule) {
                        Text(
                            text = namedScheduleData.scheduleData.scheduleEntity.timetableType.typeName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }
        },
        actions = {
            if (isSavedSchedule && isNotEmpty) {
                IconButton(
                    onClick = {
                        navigateToAddEvent(namedScheduleData.scheduleData.scheduleEntity)
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
                    onSetScheduleView(
                        scheduleView.next(isPeriodic)
                    )
                }
            ) {
                AnimatedContent(
                    targetState = scheduleView,
                    transitionSpec = {
                        scaleIn() togetherWith scaleOut()
                    }
                ) { view ->
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = when (view) {
                            ScheduleView.CALENDAR -> {
                                if (isPeriodic) ImageVector.vectorResource(R.drawable.split)
                                else ImageVector.vectorResource(R.drawable.list)
                            }

                            ScheduleView.SPLIT_WEEKS -> {
                                ImageVector.vectorResource(R.drawable.list)
                            }

                            ScheduleView.LIST -> {
                                ImageVector.vectorResource(R.drawable.calendar)
                            }
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            IconButton(
                onClick = {
                    onShowNamedScheduleDialog(
                        namedScheduleData.namedSchedule.namedScheduleEntity
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
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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