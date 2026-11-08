package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.widget

import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.egormelnikoff.schedulerutmiit.core.ui.components.button_row.AppButtonRow
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.AddScheduleAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.AddScheduleState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.addNonPeriodicScheduleStateMock

@Composable
fun AddScheduleTypeWidget(
    addScheduleState: AddScheduleState,
    onAction: (AddScheduleAction) -> Unit
) {
    AppButtonRow(
        selectedElement = addScheduleState.timetableType.isPeriodic(),
        elements = listOf(true, false),
        colors = SegmentedButtonDefaults.colors().copy(
            activeContainerColor = AppTheme.colors.primary,
            activeBorderColor = Color.Transparent,
            activeContentColor = AppTheme.colors.onPrimary,
            inactiveContainerColor = AppTheme.colors.secondaryContainer,
            inactiveBorderColor = Color.Transparent,
            inactiveContentColor = AppTheme.colors.onSecondaryContainer
        ),
        onClick = { isPeriodic ->
            onAction(
                AddScheduleAction.OnUpdateType(
                    if (isPeriodic) TimetableType.PERIODIC
                    else TimetableType.NON_PERIODIC
                )
            )
        }
    ) { value ->
        Text(
            text = if (value.second) TimetableType.PERIODIC.typeName
            else TimetableType.NON_PERIODIC.typeName,
            style = AppTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(locale = "ru", showBackground = true)
@Composable
fun AddScheduleTypeWidgetPreview() {
    PreviewLightTheme {
        AddScheduleTypeWidget(addNonPeriodicScheduleStateMock) { }
    }
}