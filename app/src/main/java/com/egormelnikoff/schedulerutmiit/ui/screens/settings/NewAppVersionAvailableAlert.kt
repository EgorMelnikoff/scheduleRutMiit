package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem

@Composable
fun NewAppVersionAvailableAlert(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        ClickableItem(
            subtitle = "Доступна новая версия",
            subtitleColor = MaterialTheme.colorScheme.onPrimary,
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            },
            onClick = onClick,
            clickLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}