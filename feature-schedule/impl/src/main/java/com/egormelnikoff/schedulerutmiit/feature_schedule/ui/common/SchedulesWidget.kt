package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.ScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule

@Composable
internal fun SchedulesWidget(
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
                                ScheduleType.GROUP -> AppTheme.icons.group

                                ScheduleType.PERSON -> AppTheme.icons.person

                                ScheduleType.ROOM -> AppTheme.icons.room

                                ScheduleType.MY -> AppTheme.icons.edit
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
                                icon =  AppTheme.icons.check,
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