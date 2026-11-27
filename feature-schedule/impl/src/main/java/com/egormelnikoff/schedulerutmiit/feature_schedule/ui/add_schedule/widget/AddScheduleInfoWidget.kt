package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R as CommonR
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.components.text_field.AppTextField
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.R
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.AddScheduleAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.AddScheduleState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.addNonPeriodicScheduleStateMock

@Composable
internal fun AddScheduleInfoWidget(
    focusManager: FocusManager,
    addScheduleState: AddScheduleState,
    onAction: (AddScheduleAction) -> Unit
) {
    ColumnGroup(
        modifier = Modifier,
        items = listOf(
            {
                AppTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = addScheduleState.name,
                    placeholderText = stringResource(R.string.schedule_name),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Done
                    )
                ) { name ->
                    onAction(AddScheduleAction.OnUpdateName(name))
                }
            }, {
                AppCell(
                    showChevron = false,
                    contentPadding = PaddingValues(
                        horizontal = AppTheme.paddings.padding12,
                        vertical = AppTheme.paddings.padding16,
                    ),
                    start = {
                        Icon(
                            icon = ImageVector.vectorResource(CommonR.drawable.calendar),
                            iconSize = 20.dp,
                            iconColor = AppTheme.colors.onBackground
                        )
                    },
                    center = {
                        TextBlock(
                            text = when {
                                addScheduleState.startDate != null && addScheduleState.startDate == addScheduleState.endDate ->
                                    "${addScheduleState.startDate.format(dayMonthYearFormatter)}"

                                addScheduleState.startDate != null && addScheduleState.endDate != null ->
                                    "${addScheduleState.startDate.format(dayMonthYearFormatter)}" +
                                            " - ${
                                                addScheduleState.endDate.format(
                                                    dayMonthYearFormatter
                                                )
                                            }"

                                else -> stringResource(R.string.schedule_dates)
                            },
                            typography = AppTheme.typography.titleSmall,
                        )
                    },
                    onClick = {
                        focusManager.clearFocus()
                        onAction(AddScheduleAction.OnShowDialogDate(true))
                    }
                )
            }
        )
    )
}

@Preview(locale = "ru", showBackground = true)
@Composable
internal fun AddScheduleInfoWidgetPreview() {
    PreviewLightTheme {
        AddScheduleInfoWidget(LocalFocusManager.current, addNonPeriodicScheduleStateMock) { }
    }
}