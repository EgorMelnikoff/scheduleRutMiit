package com.egormelnikoff.schedulerutmiit.core.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun EmptyScreen(
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    title: String? = null,
    subtitle: String? = null,
    isBoldTitle: Boolean = true,
    paddingTop: Dp = AppTheme.paddings.none,
    paddingBottom: Dp = AppTheme.paddings.none
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(
                start = AppTheme.paddings.padding32,
                end = AppTheme.paddings.padding32,
                top = paddingTop,
                bottom = paddingBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12, Alignment.CenterVertically)
    ) {
        imageVector?.let {
            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = it,
                contentDescription = null,
                tint = AppTheme.colors.onBackground
            )
        }

        title?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.onBackground,
                fontWeight = if (isBoldTitle) {
                    FontWeight.Bold
                } else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
        subtitle?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}