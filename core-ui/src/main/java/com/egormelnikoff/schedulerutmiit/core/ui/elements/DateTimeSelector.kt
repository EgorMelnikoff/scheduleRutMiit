package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.hourMinuteFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun DateTimeSelector(
    dateEvent: LocalDate?,
    startTime: LocalTime?,
    endTime: LocalTime?,
    onShowDialogDate: (Boolean) -> Unit,
    onShowDialogStartTime: (Boolean) -> Unit,
    onShowDialogEndTime: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    GridGroup(
        title = stringResource(R.string.date_and_time),
        items = listOf(
            listOf {
                ClickableItem(
                    defaultMinHeight = 32.dp,
                    showClickLabel = false,
                    title = dateEvent?.format(dayMonthYearFormatter)
                        ?: stringResource(R.string.date),
                    titleTypography = MaterialTheme.typography.titleSmall,
                    leadingIcon = {
                        LeadingIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.calendar),
                            iconSize = 20.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                ) {
                    onShowDialogDate(true)
                    focusManager.clearFocus()
                }
            },
            listOf(
                {
                    ClickableItem(
                        defaultMinHeight = 32.dp,
                        showClickLabel = false,
                        title = startTime?.format(hourMinuteFormatter)
                            ?: stringResource(R.string.start_time),
                        titleTypography = MaterialTheme.typography.titleSmall,
                        leadingIcon = {
                            LeadingIcon(
                                imageVector = ImageVector.vectorResource(R.drawable.time),
                                iconSize = 20.dp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    ) {
                        onShowDialogStartTime(true)
                        focusManager.clearFocus()
                    }
                }, {
                    ClickableItem(
                        defaultMinHeight = 32.dp,
                        showClickLabel = false,
                        title = endTime?.format(hourMinuteFormatter)
                            ?: stringResource(R.string.end_time),
                        titleTypography = MaterialTheme.typography.titleSmall,
                        leadingIcon = {
                            LeadingIcon(
                                imageVector = ImageVector.vectorResource(R.drawable.time),
                                iconSize = 20.dp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    ) {
                        onShowDialogEndTime(true)
                        focusManager.clearFocus()
                    }
                }
            )
        )
    )
}