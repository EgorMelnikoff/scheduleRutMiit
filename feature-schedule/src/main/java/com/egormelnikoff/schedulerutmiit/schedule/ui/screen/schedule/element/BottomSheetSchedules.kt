package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.SchedulesWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogSchedules(
    namedSchedules: List<NamedSchedule>,
    onClickItem: (NamedSchedule) -> Unit,
    onDismiss: () -> Unit
) {
    CustomModalBottomSheet(
        isDarkTheme = true,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        onDismiss = onDismiss
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            SchedulesWidget(
                namedSchedules = namedSchedules,
                onClickItem = onClickItem
            )
        }
    }
}