package com.egormelnikoff.schedulerutmiit.ui.dialogs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R

@Composable
fun ChooseDateTimeButton(
    modifier: Modifier,
    title: String,
    imageVector: ImageVector? = null,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .clickable(
                enabled = enabled
            ) { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = imageVector ?: ImageVector.vectorResource(R.drawable.calendar),
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onSurface
        )
        Text(
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = title,
                fontSize = 12.sp,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
