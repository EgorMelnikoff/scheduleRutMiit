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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R

@Composable
fun GridGroup(
    title: String? = null,
    titleColor: Color? = null,
    edgeCorner: Dp = 16.dp,
    interiorCorner: Dp = 4.dp,
    items: List<List<@Composable () -> Unit>>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title != null) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor ?: MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items.forEachIndexed { indexColumn, itemDataColumn ->
                val isFirstColumn = indexColumn == 0
                val isLastColumn = indexColumn == items.lastIndex

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    itemDataColumn.forEachIndexed { indexRow, itemDataRow ->
                        val isFirstRow = indexRow == 0
                        val isLastRow = indexRow == itemDataColumn.lastIndex

                        val topStartRadius = if (isFirstColumn && isFirstRow) edgeCorner else interiorCorner
                        val topEndRadius = if (isFirstColumn && isLastRow) edgeCorner else interiorCorner
                        val bottomStartRadius = if (isLastColumn && isFirstRow) edgeCorner else interiorCorner
                        val bottomEndRadius = if (isLastColumn && isLastRow) edgeCorner else interiorCorner

                        val shape = remember(topStartRadius, topEndRadius, bottomStartRadius, bottomEndRadius) {
                            RoundedCornerShape(
                                topStart = topStartRadius,
                                topEnd = topEndRadius,
                                bottomStart = bottomStartRadius,
                                bottomEnd = bottomEndRadius
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(shape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            itemDataRow()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnGroup(
    title: String? = null,
    titleColor: Color? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    edgeCorner: Dp = 16.dp,
    interiorCorner: Dp = 4.dp,
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title != null) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor ?: MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items.forEachIndexed { index, itemData ->
                val isFirst = index == 0
                val isLast = index == items.lastIndex

                val topStartRadius = if (isFirst) edgeCorner else interiorCorner
                val topEndRadius = if (isFirst) edgeCorner else interiorCorner
                val bottomStartRadius = if (isLast) edgeCorner else interiorCorner
                val bottomEndRadius = if (isLast) edgeCorner else interiorCorner

                val shape = remember(topStartRadius, topEndRadius, bottomStartRadius, bottomEndRadius) {
                    RoundedCornerShape(
                        topStart = topStartRadius,
                        topEnd = topEndRadius,
                        bottomStart = bottomStartRadius,
                        bottomEnd = bottomEndRadius
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(backgroundColor)
                ) {
                    itemData()
                }
            }
        }
    }
}

@Composable
fun RowGroup(
    title: String? = null,
    titleColor: Color? = null,
    edgeCorner: Dp = 16.dp,
    interiorCorner: Dp = 4.dp,
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title != null) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor ?: MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items.forEachIndexed { index, itemData ->
                val isFirst = index == 0
                val isLast = index == items.lastIndex

                val topStartRadius = if (isFirst) edgeCorner else interiorCorner
                val bottomStartRadius = if (isFirst) edgeCorner else interiorCorner
                val topEndRadius = if (isLast) edgeCorner else interiorCorner
                val bottomEndRadius = if (isLast) edgeCorner else interiorCorner

                val shape = remember(topStartRadius, topEndRadius, bottomStartRadius, bottomEndRadius) {
                    RoundedCornerShape(
                        topStart = topStartRadius,
                        topEnd = topEndRadius,
                        bottomStart = bottomStartRadius,
                        bottomEnd = bottomEndRadius
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surface)

                ) {
                    itemData()
                }
            }
        }
    }
}

@Composable
fun ClickableItem(
    onClick: (() -> Unit)? = null,
    padding: Dp = 12.dp,
    title: String,
    subtitle: String? = null,
    subtitleMaxLines: Int = 1,
    imageSize: Dp = 36.dp,
    imageUrl: String? = null,
    imageUrlErrorTextSize: Int = 12,
    imageVector: ImageVector? = null
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
            .padding(padding),
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
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(imageSize)
                        .let {
                            if (model.state !is AsyncImagePainter.State.Success) {
                                it.border(0.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
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
                            fontSize = imageUrlErrorTextSize.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    else -> {

                    }
                }
            }
        } ?: imageVector?.let {
            Icon(
                modifier = Modifier.size(imageSize),
                imageVector = imageVector,
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    maxLines = subtitleMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (onClick != null) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}