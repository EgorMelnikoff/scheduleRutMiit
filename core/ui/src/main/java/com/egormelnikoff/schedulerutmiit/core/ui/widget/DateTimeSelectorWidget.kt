package com.egormelnikoff.schedulerutmiit.core.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.hourMinuteFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.GridGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun DateTimeSelectorWidget(
    dateEvent: LocalDate?,
    startTime: LocalTime?,
    endTime: LocalTime?,
    onShowDialogDate: (Boolean) -> Unit,
    onShowDialogStartTime: (Boolean) -> Unit,
    onShowDialogEndTime: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    GridGroup(
        title = stringResource(R.string.ui_date_and_time),
        items = listOf(
            listOf {
                AppCell(
                    showChevron = false,
                    start = {
                        Icon(
                            icon = AppTheme.icons.calendar,
                            iconSize = 20.dp,
                            iconColor = AppTheme.colors.onBackground
                        )
                    },
                    center = {
                        TextBlock(
                            text = dateEvent?.format(dayMonthYearFormatter)
                                ?: stringResource(R.string.ui_date),
                            typography = AppTheme.typography.titleSmall,
                        )
                    },
                    onClick = {
                        onShowDialogDate(true)
                        focusManager.clearFocus()
                    }
                )
            },
            listOf(
                {
                    AppCell(
                        showChevron = false,
                        start = {
                            Icon(
                                icon = AppTheme.icons.time,
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onBackground
                            )
                        },
                        center = {
                            TextBlock(
                                text = startTime?.format(hourMinuteFormatter)
                                    ?: stringResource(R.string.ui_start_time),
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
                                icon = AppTheme.icons.time,
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onBackground
                            )
                        },
                        center = {
                            TextBlock(
                                text = endTime?.format(hourMinuteFormatter)
                                    ?: stringResource(R.string.ui_end_time),
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
    )
}