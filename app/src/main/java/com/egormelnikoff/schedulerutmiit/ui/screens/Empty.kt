package com.egormelnikoff.schedulerutmiit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Empty(
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    title: String? = null,
    subtitle: String? = null,
    isBoldTitle: Boolean = true,
    paddingTop: Dp? = null,
    paddingBottom: Dp? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = 32.dp,
                end = 32.dp,
                top = paddingTop ?: 0.dp,
                bottom = paddingBottom ?: 0.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        imageVector?.let {
            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        title?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}