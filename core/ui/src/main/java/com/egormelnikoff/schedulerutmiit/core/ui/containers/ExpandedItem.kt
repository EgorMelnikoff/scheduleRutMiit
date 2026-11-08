package com.egormelnikoff.schedulerutmiit.core.ui.containers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun ExpandedItem(
    title: String,
    imageVector: ImageVector? = null,
    visible: Boolean,
    onChangeVisibility: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (visible) 180f else 0f,
    )
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shapes.small)
                .clickable { onChangeVisibility(!visible) }
                .padding(AppTheme.paddings.padding4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
        ) {
            if (imageVector != null) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = imageVector,
                    tint = AppTheme.colors.onSecondaryContainer,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.onSecondaryContainer,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(
                        rotationZ = rotationAngle
                    ),
                imageVector = ImageVector.vectorResource(R.drawable.down),
                contentDescription = null
            )
        }
        SpacerHeight(AppTheme.paddings.padding8)
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                content.invoke()
                SpacerHeight(AppTheme.paddings.padding8)
            }
        }
    }
}
