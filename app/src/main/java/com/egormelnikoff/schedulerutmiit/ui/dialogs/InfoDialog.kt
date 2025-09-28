package com.egormelnikoff.schedulerutmiit.ui.dialogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.APP_GITHUB_REPOS
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.CLOUD_TIPS
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.SimpleTopBar
import com.egormelnikoff.schedulerutmiit.ui.view_models.AppInfoState

@Composable
fun InfoDialog(
    onBack: () -> Unit,
    appInfoState: AppInfoState,
    paddingValues: PaddingValues,
    uriHandler: UriHandler
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = LocalContext.current.getString(R.string.about_app),
                navAction = { onBack() },
                navImageVector = ImageVector.vectorResource(R.drawable.back)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .scale(0.6f)
                        .size(84.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.logo),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = LocalContext.current.getString(R.string.app_name),
                        fontSize = 20.sp,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        text = "${packageInfo.versionName} (${getLongVersionCode(packageInfo)})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            ColumnGroup(
                items = listOf(
                    {
                        Link(
                            icon = {
                                Icon(
                                    modifier = Modifier.size(36.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.logo_telegram),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            },
                            title = LocalContext.current.getString(R.string.telegram),
                            subtitle = LocalContext.current.getString(R.string.news_updates),
                            onClick = {
                                uriHandler.openUri(APP_CHANNEL_URL)
                            }
                        )
                    }, {
                        Link(
                            icon = {
                                Icon(
                                    modifier = Modifier.size(36.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.logo_github),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            title = LocalContext.current.getString(R.string.github),
                            subtitle = LocalContext.current.getString(R.string.source_code),
                            onClick = {
                                uriHandler.openUri(APP_GITHUB_REPOS)
                            }
                        )
                    }
                )
            )

            ColumnGroup(
                items = listOf {
                    Link(
                        icon = {
                            Icon(
                                modifier = Modifier.size(36.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.ruble),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        title = LocalContext.current.getString(R.string.support),
                        onClick = {
                            uriHandler.openUri(CLOUD_TIPS)
                        }
                    )
                }
            )

            when (appInfoState) {
                is AppInfoState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                }

                is AppInfoState.Loaded -> {
                    if (appInfoState.authorTelegramPage != null) {
                        ColumnGroup(
                            items = listOf {
                                Link(
                                    icon = {
                                        val model =
                                            rememberAsyncImagePainter(appInfoState.authorTelegramPage.imageUrl!!)
                                        val transition by animateFloatAsState(
                                            targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
                                        )
                                        Image(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .alpha(transition),
                                            contentScale = ContentScale.Crop,
                                            painter = model,
                                            contentDescription = null,
                                        )
                                    },
                                    title = appInfoState.authorTelegramPage.name!!,
                                    subtitle = LocalContext.current.getString(R.string.author),
                                    onClick = {
                                        appInfoState.authorTelegramPage.url?.let {
                                            uriHandler.openUri(
                                                it
                                            )
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Link(
    icon: @Composable (() -> Unit),
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        icon.invoke()
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
                    maxLines = 1,
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
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
