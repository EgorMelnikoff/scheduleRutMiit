package com.egormelnikoff.schedulerutmiit.core.ui.components.snackbar_host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun AppSnackbarHost(
    snackBarHostState: SnackbarHostState,
) {
    SnackbarHost(
        modifier = Modifier.padding(horizontal = AppTheme.paddings.padding24),
        hostState = snackBarHostState
    ) { data ->
        val visuals = data.visuals as? AppSnackbarVisuals
        val isError = visuals?.isError ?: false

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = if (isError) AppTheme.colors.error
                    else AppTheme.colors.primary,
                    shape = AppTheme.shapes.medium
                )
                .padding(horizontal = AppTheme.paddings.padding12, vertical = AppTheme.paddings.padding4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = data.visuals.message,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onPrimary
            )
            IconButton(
                onClick = {
                    data.dismiss()
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.clear),
                    contentDescription = null,
                    tint = AppTheme.colors.onPrimary
                )
            }
        }
    }
}