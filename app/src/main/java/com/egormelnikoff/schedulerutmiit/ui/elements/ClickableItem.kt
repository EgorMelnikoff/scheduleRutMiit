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
import androidx.compose.material3.CircularProgressIndicator
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
    title: String,
    titleTypography: TextStyle? = null,
    titleMaxLines: Int = 1,
    titleColor: Color? = null,
    subtitle: String? = null,
    subtitleMaxLines: Int = 1,
    subtitleLabel: (@Composable () -> Unit)? = null,
    imageSize: Dp = 36.dp,
    imageUrl: String? = null,
    imageUrlErrorTextSize: Int = 12,
    imageVector: ImageVector? = null,
    imageVectorColor: Color? = null,
    showClickLabel: Boolean = true,
    defaultMinHeight: Dp? = null,
    trailingIcon: (@Composable () -> Unit)? = null
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
        imageUrl?.let {
            val model = rememberAsyncImagePainter(imageUrl)
            val transition by animateFloatAsState(
                targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
            )
            Box(
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(imageSize)
                        .let {
                            if (model.state !is AsyncImagePainter.State.Success) {
                                it.border(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.onSecondaryContainer,
                                    CircleShape
                                )
                            } else it
                        }
                        .alpha(transition),
                    contentScale = ContentScale.Crop,
                    painter = model,
                    contentDescription = null,
                )
                when (model.state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    is AsyncImagePainter.State.Error, AsyncImagePainter.State.Empty -> {
                        Text(
                            text = title.first().toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                            fontSize = imageUrlErrorTextSize.sp
                        )
                    }

                    else -> {}
                }
            }
        } ?: imageVector?.let {
            Icon(
                modifier = Modifier.size(imageSize),
                imageVector = imageVector,
                contentDescription = null,
                tint = imageVectorColor ?: Color.Unspecified
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = titleTypography ?: MaterialTheme.typography.titleMedium,
                color = titleColor ?: MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = titleMaxLines
            )
            subtitle?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subtitleLabel?.invoke()
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = subtitleMaxLines
                    )
                }
            }
        }
        trailingIcon?.let {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                trailingIcon.invoke()
            }
        }
        if (showClickLabel && onClick != null) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}