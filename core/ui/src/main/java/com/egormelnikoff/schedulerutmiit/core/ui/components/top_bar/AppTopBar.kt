package com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titleText: String? = null,
    subtitleText: String? = null,
    shadowElevation: Dp = 0.dp,
    navAction: (() -> Unit)? = null,
    navImageVector: ImageVector = ImageVector.vectorResource(R.drawable.back),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentColor: Color = AppTheme.colors.onBackground,
    showTitleChevron: Boolean = false,
    onTitleClick: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(
            bottomStart = AppTheme.radius.radius16,
            bottomEnd = AppTheme.radius.radius16
        ),
        shadowElevation = shadowElevation,
        color = Color.Unspecified,
        contentColor = Color.Unspecified
    ) {
        TopAppBar(
            title = {
                titleText?.let {
                    Column(
                        modifier = Modifier.clickable(
                            enabled = onTitleClick != null,
                            indication = null,
                            interactionSource = null,
                        ) {
                            onTitleClick?.invoke()
                        },
                        verticalArrangement = spacedBy(AppTheme.paddings.padding4)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                        ) {
                            Text(
                                text = titleText,
                                style = AppTheme.typography.titleLarge,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            if (onTitleClick != null && showTitleChevron) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.right),
                                    contentDescription = null
                                )
                            }
                        }
                        subtitleText?.let {
                            Text(
                                text = subtitleText,
                                style = AppTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                navAction?.let {
                    IconButton(
                        onClick = it
                    ) {
                        Icon(
                            imageVector = navImageVector,
                            contentDescription = null
                        )
                    }
                }
            },
            actions = {
                actions?.invoke()
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = if (scrollBehavior != null) Color.Transparent else AppTheme.colors.background,
                scrolledContainerColor = AppTheme.colors.background,
                titleContentColor = contentColor,
                navigationIconContentColor = contentColor,
                actionIconContentColor = contentColor
            ),
            scrollBehavior = scrollBehavior
        )
    }
}