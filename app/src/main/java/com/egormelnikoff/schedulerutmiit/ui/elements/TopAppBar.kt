package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.NamedScheduleUiDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    titleText: String? = null,
    subtitleText: String? = null,
    navAction: (() -> Unit)? = null,
    navImageVector: ImageVector = ImageVector.vectorResource(R.drawable.back),
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            titleText?.let {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    subtitleText?.let {
                        Text(
                            text = subtitleText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
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
    navigateToHiddenEvents: (NamedScheduleEntity) -> Unit,
    onSetScheduleView: (ScheduleView) -> Unit,
    onShowNamedScheduleDialog: (NamedScheduleEntity) -> Unit,

    namedScheduleUiDto: NamedScheduleUiDto,
    scheduleView: ScheduleView,
    isPeriodic: Boolean
) {
    val isCustomSchedule =
        namedScheduleUiDto.namedSchedule.namedScheduleEntity.type == NamedScheduleType.MY

    CustomTopAppBar(
        titleText = namedScheduleUiDto.namedSchedule.namedScheduleEntity.shortName,
        subtitleText = if (namedScheduleUiDto.scheduleUiDto?.scheduleEntity != null && !isCustomSchedule) {
            namedScheduleUiDto.scheduleUiDto.scheduleEntity.timetableType.typeName
        } else null,
        actions = {
            AnimatedVisibility(
                visible = !namedScheduleUiDto.scheduleUiDto?.hiddenEvents.isNullOrEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        navigateToHiddenEvents(namedScheduleUiDto.namedSchedule.namedScheduleEntity)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.visibility_off),
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
                        namedScheduleUiDto.namedSchedule.namedScheduleEntity
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