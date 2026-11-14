package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulesSheetWidget(
    namedSchedules: List<NamedSchedule>,
    onDismiss: (NamedSchedule?) -> Unit
) {
    AppBottomSheet(
        showHandle = false,
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding16),
        onDismiss = {
            onDismiss(null)
        }
    ) {

    }
}