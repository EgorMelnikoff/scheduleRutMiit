package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun <T> ListInputWidget(
    title: String,
    onAddElement: () -> Unit,
    maxCount: Int,
    elements: List<T>,
    elementContent: @Composable (index: Int, element: T) -> Unit
) {
    val enabledAdd = remember(maxCount, elements.size) {
        elements.size < maxCount
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.onSecondaryContainer,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            OutlinedIconButton(
                onClick = onAddElement,
                enabled = enabledAdd,
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = AppTheme.colors.primary,
                    contentColor = AppTheme.colors.onPrimary,
                    disabledContentColor = AppTheme.colors.outline
                ),
                border = if (!enabledAdd) {
                    BorderStroke(0.5.dp, AppTheme.colors.outline)
                } else null
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.add),
                    contentDescription = null
                )
            }
        }
        Column(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
        ) {
            elements.forEachIndexed { index, element ->
                elementContent(index, element)
            }
        }
    }
}