package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule

@Composable
fun SchedulesWidget(
    namedSchedules: List<NamedSchedule>,
    onClickItem: (NamedSchedule) -> Unit
) {
    ColumnGroup(
        items = namedSchedules.map { namedSchedule ->
            {
                AppCell(
                    start = {
                        Icon(
                            icon = when (namedSchedule.type) {
                                NamedScheduleType.GROUP -> ImageVector.vectorResource(
                                    R.drawable.group
                                )

                                NamedScheduleType.PERSON -> ImageVector.vectorResource(
                                    R.drawable.person
                                )

                                NamedScheduleType.ROOM -> ImageVector.vectorResource(
                                    R.drawable.room
                                )

                                NamedScheduleType.MY -> ImageVector.vectorResource(
                                    R.drawable.edit
                                )
                            },
                            iconSize = 16.dp,
                            iconColor = AppTheme.colors.onSecondaryContainer
                        )
                    },
                    center = {
                        TextBlock(
                            text = namedSchedule.shortName,
                            maxLines = 1,
                        )
                    },
                    end = {
                        if (namedSchedule.isDefault) {
                            Icon(
                                icon =  ImageVector.vectorResource(R.drawable.check),
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.primary
                            )
                        }
                    },
                    onClick = {
                        onClickItem(namedSchedule)
                    }
                )
            }
        }
    )
}