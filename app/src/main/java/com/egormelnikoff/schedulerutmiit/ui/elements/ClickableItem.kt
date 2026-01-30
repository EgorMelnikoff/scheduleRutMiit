package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R

@Composable
fun ClickableItem(
    onClick: (() -> Unit)? = null,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 12.dp,
    defaultMinHeight: Dp? = null,

    title: String? = null,
    titleTypography: TextStyle? = null,
    titleMaxLines: Int = 1,
    titleColor: Color? = null,
    titleLabel: (@Composable () -> Unit)? = null,

    subtitle: String? = null,
    subtitleTypography: TextStyle? = null,
    subtitleMaxLines: Int = 1,
    subtitleColor: Color? = null,
    subtitleLabel: (@Composable () -> Unit)? = null,

    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    showClickLabel: Boolean = true,
    clickLabelColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (onClick != null) {
                    it.clickable { onClick() }
                } else {
                    it
                }
            }
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .let {
                if (defaultMinHeight != null) {
                    it.defaultMinSize(
                        minHeight = defaultMinHeight
                    )
                } else it
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            leadingIcon?.invoke()
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            title?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = titleTypography ?: MaterialTheme.typography.titleMedium,
                        color = titleColor ?: MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = titleMaxLines
                    )
                    titleLabel?.invoke()
                }
            }
            subtitle?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subtitleLabel?.invoke()
                    Text(
                        text = subtitle,
                        style = subtitleTypography ?: MaterialTheme.typography.bodyMedium,
                        color = subtitleColor ?: MaterialTheme.colorScheme.onSecondaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = subtitleMaxLines
                    )
                }
            }
        }
        trailingIcon?.let {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                it.invoke()
            }
        }
        if (showClickLabel && onClick != null) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.right),
                contentDescription = null,
                tint = clickLabelColor ?: MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun LeadingAsyncImage(
    title: String,
    imageUrl: String? = null,
    imageSize: Dp = 32.dp,
    titleSize: TextUnit = 12.sp,
) {
    val painter = rememberAsyncImagePainter(imageUrl)
    val transition by animateFloatAsState(
        targetValue = if (painter.state is AsyncImagePainter.State.Success) 1f else 0f
    )

    Box(
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
                .alpha(transition),
            contentScale = ContentScale.Crop,
            painter = painter,
            contentDescription = title,
        )
        if (painter.state !is AsyncImagePainter.State.Success) {
            LeadingTitle(
                title = title,
                titleSize = titleSize,
                imageSize = imageSize,
            )
        }
    }
}

@Composable
fun LeadingTitle(
    title: String,
    titleSize: TextUnit = 12.sp,
    imageSize: Dp = 32.dp
) {
    Box(
        modifier = Modifier
            .size(imageSize)
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.onSecondaryContainer,
                CircleShape
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.first().uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            fontSize = titleSize
        )
    }
}

@Composable
fun LeadingIcon(
    imageVector: ImageVector,
    iconSize: Dp = 36.dp,
    color: Color = Color.Unspecified
) {
    Icon(
        modifier = Modifier.size(iconSize),
        imageVector = imageVector,
        contentDescription = null,
        tint = color
    )
}