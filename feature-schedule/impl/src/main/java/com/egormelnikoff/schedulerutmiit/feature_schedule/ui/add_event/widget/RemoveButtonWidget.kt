package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R

@Composable
fun RemoveButtonWidget(
    onRemove: () -> Unit
) {
    OutlinedIconButton(
        onClick = onRemove,
        border = BorderStroke(width = 0.5.dp, MaterialTheme.colorScheme.error),
        colors = IconButtonDefaults.outlinedIconButtonColors().copy(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.delete),
            contentDescription = null
        )
    }
}
