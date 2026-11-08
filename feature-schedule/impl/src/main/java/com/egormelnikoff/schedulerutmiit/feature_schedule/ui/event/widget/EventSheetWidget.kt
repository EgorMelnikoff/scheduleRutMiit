package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheetButton
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetEventWidget(
    timetableType: TimetableType,
    event: Event,
    onHideEvent: (() -> Unit)? = null,
    onDeleteEvent: (() -> Unit)? = null,
    onEditEvent: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    AppBottomSheet(
        onDismiss = {
            onDismiss()
        }
    ) {
        EventNameWidget(
            timetableType, event, 16.dp
        )
        SpacerHeight(0.dp)
        onHideEvent?.let {
            AppBottomSheetButton(
                icon = ImageVector.vectorResource(R.drawable.visibility_off),
                title = stringResource(R.string.hide_event),
                contentColor = AppTheme.colors.onBackground
            ) {
                onHideEvent()
                onDismiss()
            }
        }
        onEditEvent?.let {
            AppBottomSheetButton(
                icon = ImageVector.vectorResource(R.drawable.edit),
                title = stringResource(R.string.edit),
                contentColor = AppTheme.colors.onBackground
            ) {
                onEditEvent()
                onDismiss()
            }
        }
        onDeleteEvent?.let {
            AppBottomSheetButton(
                icon = ImageVector.vectorResource(R.drawable.delete),
                title = stringResource(R.string.delete_event),
                contentColor = AppTheme.colors.error
            ) {
                onDeleteEvent()
                onDismiss()
            }
        }
    }
}
