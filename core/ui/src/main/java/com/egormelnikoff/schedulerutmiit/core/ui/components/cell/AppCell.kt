package com.egormelnikoff.schedulerutmiit.core.ui.components.cell

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.Red

interface AppCellScope : RowScope
interface AppCellCenterScope : RowScope
interface AppCellStartScope : AppCellScope
interface AppCellEndScope : AppCellScope

private class AppCellScopeImpl(rowScope: RowScope) : AppCellScope, RowScope by rowScope

private class AppCellStartScopeImpl(rowScope: RowScope) : AppCellStartScope,
    AppCellScope by AppCellScopeImpl(rowScope)

private class AppCellEndScopeImpl(rowScope: RowScope) : AppCellEndScope,
    AppCellScope by AppCellScopeImpl(rowScope)

private class AppCellCenterScopeImpl(rowScope: RowScope) : AppCellCenterScope, RowScope by rowScope

@Composable
fun AppCell(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AppTheme.paddings.padding12,
        vertical = AppTheme.paddings.padding12,
    ),
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    showChevron: Boolean = true,
    showSeparator: Boolean = false,
    start: @Composable AppCellStartScope.() -> Unit = {},
    end: @Composable AppCellEndScope.() -> Unit = {},
    center: @Composable AppCellCenterScope.() -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .let { modifier ->
                    onClick?.let {
                        modifier.combinedClickable(
                            onClick = onClick,
                            onLongClick = onLongClick,
                        )
                    } ?: modifier
                }
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
        ) {
            val startScope = AppCellStartScopeImpl(this)
            val centerScope = AppCellCenterScopeImpl(this)
            val endScope = AppCellEndScopeImpl(this)

            startScope.start()

            Box(
                modifier = Modifier.weight(1f)
            ) {
                centerScope.center()
            }

            endScope.end()

            if (showChevron && onClick != null) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = AppTheme.icons.right,
                    contentDescription = null,
                    tint = AppTheme.colors.onSecondaryContainer,
                )
            }
        }

        if (showSeparator) {
            HorizontalDivider(
                thickness = 0.dp,
                color = AppTheme.colors.outline
            )
        }
    }
}


@Composable
fun AppCellScope.Custom(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        content()
    }
}

@Composable
fun AppCellScope.Icon(
    icon: ImageVector,
    iconSize: Dp = 24.dp,
    iconColor: Color = Color.Unspecified,
    showBadge: Boolean = false
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        BadgedBox(
            badge = {
                if (showBadge) {
                    Badge(
                        containerColor = Red,
                        contentColor = Color.Unspecified
                    )
                }
            }
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
        }
    }
}

@Composable
fun AppCellScope.IconButton(
    icon: ImageVector,
    iconSize: Dp = 24.dp,
    iconButtonColors: IconButtonColors = IconButtonDefaults.iconButtonColors().copy(
        contentColor = AppTheme.colors.onSecondaryContainer
    ),
    onClick: () -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        IconButton(
            modifier = Modifier.size(36.dp),
            colors = iconButtonColors,
            onClick = onClick
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = icon,
                contentDescription = null,
            )
        }
    }
}


@Composable
fun AppCellStartScope.AsyncImage(
    imageUrl: String,
    imageTitle: String,
    imageSize: Dp = 24.dp,
    titleSize: TextUnit = 12.sp,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
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
                contentDescription = null,
            )

            if (painter.state !is AsyncImagePainter.State.Success) {
                this@AsyncImage.Title(
                    title = imageTitle.firstOrNull() ?: ' ',
                    titleSize = titleSize,
                    imageSize = imageSize,
                )
            }
        }
    }
}

@Composable
private fun AppCellStartScope.Title(
    title: Char,
    titleSize: TextUnit = 12.sp,
    imageSize: Dp = 32.dp
) {
    Box(
        modifier = Modifier
            .size(imageSize)
            .border(
                0.5.dp,
                AppTheme.colors.onSecondaryContainer,
                CircleShape
            )
            .clip(CircleShape)
            .background(AppTheme.colors.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.uppercase(),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.onSecondaryContainer,
            maxLines = 1,
            fontSize = titleSize
        )
    }
}


@Composable
fun <T> AppCellScope.AnimatedIcon(
    targetState: T,
    iconSize: Dp = 20.dp,
    color: Color = AppTheme.colors.onSecondaryContainer,
    transitionSpec: AnimatedContentTransitionScope<T>.() -> ContentTransform = {
        scaleIn() togetherWith scaleOut()
    },
    iconProvider: @Composable (T) -> ImageVector
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        AnimatedContent(
            targetState = targetState,
            transitionSpec = transitionSpec,
            label = "StartAnimatedIcon"
        ) { state ->
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = iconProvider(state),
                contentDescription = null,
                tint = color
            )
        }
    }
}


@Composable
fun AppCellCenterScope.CenterContent(
    title: @Composable (AppCellCenterScope.() -> Unit)? = null,
    subtitle: @Composable (AppCellCenterScope.() -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AppTheme.paddings.padding4)
) {
    Column(
        verticalArrangement = verticalArrangement
    ) {
        title?.invoke(this@CenterContent)
        subtitle?.invoke(this@CenterContent)
    }
}

@Composable
fun AppCellCenterScope.TextBlock(
    modifier: Modifier = Modifier,
    text: String? = null,
    decoration: TextDecoration? = null,
    typography: TextStyle = AppTheme.typography.titleMedium,
    maxLines: Int = 1,
    label: (@Composable () -> Unit)? = null,
    color: Color = AppTheme.colors.onBackground,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
    ) {
        text?.let {
            Text(
                text = text,
                textDecoration = decoration,
                style = typography,
                color = color,
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLines
            )
        }

        AnimatedVisibility(
            visible = label != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            label?.invoke()
        }
    }
}