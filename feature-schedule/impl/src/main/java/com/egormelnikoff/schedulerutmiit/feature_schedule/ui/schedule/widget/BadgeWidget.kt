package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
internal fun BadgeWidget(
    title: String
) {
    Row(
        modifier = Modifier
            .clip(AppTheme.shapes.small)
            .background(AppTheme.colors.secondaryContainer)
            .padding(
                horizontal = AppTheme.paddings.padding8,
                vertical = AppTheme.paddings.padding4
            ),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}