package com.egormelnikoff.schedulerutmiit.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.APP_GITHUB_REPOS
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.CLOUD_TIPS
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.view_models.settings.AppInfoState

@Composable
fun InfoDialog(
    externalPadding: PaddingValues,
    onBack: () -> Unit,
    onOpenUri: (String) -> Unit,
    appInfoState: AppInfoState
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = LocalContext.current.getString(R.string.about_app),
                navAction = { onBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding(),
                    bottom = externalPadding.calculateBottomPadding()
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
                        ClickableItem(
                            title = LocalContext.current.getString(R.string.telegram),
                            subtitle = LocalContext.current.getString(R.string.news_updates),
                            imageVector = ImageVector.vectorResource(R.drawable.logo_telegram),
                            onClick = {
                                onOpenUri(APP_CHANNEL_URL)
                            }
                        )
                    }, {
                        ClickableItem(
                            title = LocalContext.current.getString(R.string.github),
                            subtitle = LocalContext.current.getString(R.string.source_code),
                            imageVector = ImageVector.vectorResource(R.drawable.logo_github),
                            onClick = {
                                onOpenUri(APP_GITHUB_REPOS)
                            },

                        )
                    }
                )
            )

            ColumnGroup(
                items = listOf {
                    ClickableItem(
                        title = LocalContext.current.getString(R.string.support),
                        imageVector = ImageVector.vectorResource(R.drawable.ruble),
                        onClick = {
                            onOpenUri(CLOUD_TIPS)
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
                                ClickableItem(
                                    title = appInfoState.authorTelegramPage.name!!,
                                    subtitle = LocalContext.current.getString(R.string.author),
                                    imageUrl = appInfoState.authorTelegramPage.imageUrl!!,
                                    onClick = {
                                        appInfoState.authorTelegramPage.url?.let {
                                            onOpenUri(it)
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