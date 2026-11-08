package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
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
                    title = namedSchedule.shortName,
                    titleMaxLines = 1,
                    defaultMinHeight = 32.dp,
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = when (namedSchedule.type) {
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
                            contentDescription = null,
                            tint = AppTheme.colors.onSecondaryContainer
                        )
                    },
                    trailingIcon = if (namedSchedule.isDefault) {
                        {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = ImageVector.vectorResource(
                                    R.drawable.check
                                ),
                                contentDescription = null,
                                tint = AppTheme.colors.primary
                            )
                        }
                    } else null,
                    onClick = {
                        onClickItem(namedSchedule)
                    }
                )
            }
        }
    )
}