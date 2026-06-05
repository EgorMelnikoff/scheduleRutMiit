package com.egormelnikoff.schedulerutmiit.schedule.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleUiDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTopAppBar(
    onSetScheduleView: (ScheduleView) -> Unit,
    onShowNamedScheduleDialog: (NamedSchedule) -> Unit,

    namedScheduleWithSchedules: NamedScheduleWithSchedules,
    scheduleUiDto: ScheduleUiDto?,
    scheduleView: ScheduleView
) {
    val isCustomSchedule = namedScheduleWithSchedules.namedSchedule.type == NamedScheduleType.MY

    CustomTopAppBar(
        titleText = namedScheduleWithSchedules.namedSchedule.shortName,
        subtitleText = if (scheduleUiDto?.schedule != null && !isCustomSchedule) {
            scheduleUiDto.schedule.timetableType.typeName
        } else null,
        actions = {
            IconButton(
                onClick = {
                    onSetScheduleView(
                        scheduleView.next()
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
                        namedScheduleWithSchedules.namedSchedule
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