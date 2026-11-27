package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
internal fun RemoveButtonWidget(
    onRemove: () -> Unit
) {
    OutlinedIconButton(
        onClick = onRemove,
        border = BorderStroke(width = 0.5.dp, AppTheme.colors.error),
        colors = IconButtonDefaults.outlinedIconButtonColors().copy(
            contentColor = AppTheme.colors.error
        )
    ) {
        Icon(
            imageVector = AppTheme.icons.delete,
            contentDescription = null
        )
    }
}
