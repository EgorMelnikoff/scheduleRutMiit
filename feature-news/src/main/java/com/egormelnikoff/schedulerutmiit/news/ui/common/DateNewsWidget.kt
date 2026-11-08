package com.egormelnikoff.schedulerutmiit.news.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
internal fun DateNewsWidget(
    date: String
) {
    Row(
        modifier = Modifier
            .clip(AppTheme.shapes.small)
            .background(AppTheme.colors.primary)
            .padding(AppTheme.paddings.padding8),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = ImageVector.vectorResource(R.drawable.schedule),
            contentDescription = null,
            tint = AppTheme.colors.onPrimary
        )
        Text(
            text = date,
            style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.onPrimary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}