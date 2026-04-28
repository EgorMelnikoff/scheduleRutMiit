package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R

@Composable
fun <T> ListParam(
    title: String,
    onAddElement: () -> Unit,
    maxCount: Int,
    elements: List<T>,
    elementContent: @Composable (index: Int, element: T) -> Unit
) {
    val enabledAdd = elements.size < maxCount
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            OutlinedIconButton(
                onClick = onAddElement,
                enabled = enabledAdd,
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                ),
                border = if (!enabledAdd) {
                    BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            elements.forEachIndexed { index, element ->
                elementContent(index, element)
            }
        }
    }
}

@Composable
fun RemoveButton(
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
