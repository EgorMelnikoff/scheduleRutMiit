package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.element

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTopAppBar(
    onSetScheduleView: (ScheduleView) -> Unit,
    onShowNamedScheduleDialog: (Boolean) -> Unit,

    namedSchedule: NamedSchedule,
    scheduleTypeName: String?,
    scheduleView: ScheduleView
) {
    val isCustomSchedule = remember(namedSchedule.type) {
        namedSchedule.type == NamedScheduleType.MY
    }

    CustomTopAppBar(
        titleText = namedSchedule.shortName,
        subtitleText = if (scheduleTypeName != null && !isCustomSchedule) {
            scheduleTypeName
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
                    onShowNamedScheduleDialog(namedSchedule.isDefault)
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