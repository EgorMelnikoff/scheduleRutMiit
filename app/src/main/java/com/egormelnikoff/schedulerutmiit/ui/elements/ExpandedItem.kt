package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.egormelnikoff.schedulerutmiit.R

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
                .clip(MaterialTheme.shapes.medium)
                .clickable { onChangeVisibility(!visible) }
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (imageVector != null) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = imageVector,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(
                        rotationZ = rotationAngle
                    ),
                imageVector = ImageVector.vectorResource(R.drawable.down),
                contentDescription = null
            )
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                content.invoke()
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }
    }
}
