package com.egormelnikoff.schedulerutmiit.core.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.hourMinuteFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.RowGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import java.time.LocalTime

@Composable
fun TimeSelector(
    startTime: LocalTime?,
    endTime: LocalTime?,
    onShowDialogStartTime: (Boolean) -> Unit,
    onShowDialogEndTime: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    RowGroup(
        title = stringResource(R.string.time),
        items = listOf(
            {
                AppCell(
                    showChevron = false,
                    start = {
                        Icon(
                            icon = ImageVector.vectorResource(R.drawable.time),
                            iconSize = 20.dp,
                            iconColor = AppTheme.colors.onBackground
                        )
                    },
                    center = {
                        TextBlock(
                            text = startTime?.format(hourMinuteFormatter)
                                ?: stringResource(R.string.start_time),
                            typography = AppTheme.typography.titleSmall,
                        )
                    },
                    onClick = {
                        onShowDialogStartTime(true)
                        focusManager.clearFocus()
                    }
                )
            }, {
                AppCell(
                    showChevron = false,
                    start = {
                        Icon(
                            icon = ImageVector.vectorResource(R.drawable.time),
                            iconSize = 20.dp,
                            iconColor = AppTheme.colors.onBackground
                        )
                    },
                    center = {
                        TextBlock(
                            text = endTime?.format(hourMinuteFormatter)
                                ?: stringResource(R.string.end_time),
                            typography = AppTheme.typography.titleSmall,
                        )
                    },
                    onClick = {
                        onShowDialogEndTime(true)
                        focusManager.clearFocus()
                    }
                )
            }
        )
    )
}