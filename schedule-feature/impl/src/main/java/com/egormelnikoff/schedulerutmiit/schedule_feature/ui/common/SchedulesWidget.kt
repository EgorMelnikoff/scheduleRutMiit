package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType

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
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
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
                                tint = MaterialTheme.colorScheme.primary
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