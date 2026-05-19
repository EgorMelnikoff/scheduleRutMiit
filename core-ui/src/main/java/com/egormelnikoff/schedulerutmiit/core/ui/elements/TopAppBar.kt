package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.theme.largeCornerRadius

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    titleText: String? = null,
    subtitleText: String? = null,
    shadowElevation: Dp = 0.dp,
    navAction: (() -> Unit)? = null,
    navImageVector: ImageVector = ImageVector.vectorResource(R.drawable.back),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    actions: @Composable (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(
            bottomStart = largeCornerRadius,
            bottomEnd = largeCornerRadius
        ),
        shadowElevation = shadowElevation,
        color = Color.Unspecified,
        contentColor = Color.Unspecified
    ) {
        TopAppBar(
            title = {
                titleText?.let {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        subtitleText?.let {
                            Text(
                                text = subtitleText,
                                style = MaterialTheme.typography.bodyMedium,
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
                containerColor = if (scrollBehavior != null) Color.Transparent else MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = contentColor,
                navigationIconContentColor = contentColor,
                actionIconContentColor = contentColor
            ),
            scrollBehavior = scrollBehavior
        )
    }
}