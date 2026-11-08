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
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.LeadingIcon
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
                    defaultMinHeight = 32.dp,
                    showClickLabel = false,
                    title = startTime?.format(hourMinuteFormatter)
                        ?: stringResource(R.string.start_time),
                    titleTypography = AppTheme.typography.titleSmall,
                    leadingIcon = {
                        LeadingIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.time),
                            iconSize = 20.dp,
                            color = AppTheme.colors.onBackground
                        )
                    }
                ) {
                    onShowDialogStartTime(true)
                    focusManager.clearFocus()
                }
            }, {
                AppCell(
                    defaultMinHeight = 32.dp,
                    showClickLabel = false,
                    title = endTime?.format(hourMinuteFormatter)
                        ?: stringResource(R.string.end_time),
                    titleTypography = AppTheme.typography.titleSmall,
                    leadingIcon = {
                        LeadingIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.time),
                            iconSize = 20.dp,
                            color = AppTheme.colors.onBackground
                        )
                    }
                ) {
                    onShowDialogEndTime(true)
                    focusManager.clearFocus()
                }
            }
        )
    )
}