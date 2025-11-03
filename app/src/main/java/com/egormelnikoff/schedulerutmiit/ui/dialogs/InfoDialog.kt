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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.AppConst.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.app.AppConst.APP_GITHUB_REPOS
import com.egormelnikoff.schedulerutmiit.app.AppConst.CLOUD_TIPS
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingAsyncImage
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.view_models.settings.AppInfoState

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
                        .size(84.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.logo_app),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = LocalContext.current.getString(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = "${packageInfo.versionName} (${getLongVersionCode(packageInfo)})",
                        style = MaterialTheme.typography.titleMedium,
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
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.logo_telegram)
                                )
                            },
                            onClick = {
                                onOpenUri(APP_CHANNEL_URL)
                            }
                        )
                    }, {
                        ClickableItem(
                            title = LocalContext.current.getString(R.string.github),
                            subtitle = LocalContext.current.getString(R.string.source_code),
                            leadingIcon ={
                                LeadingIcon(
                                    imageVector =  ImageVector.vectorResource(R.drawable.logo_github),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
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
                        leadingIcon = {
                            LeadingIcon(
                                imageVector = ImageVector.vectorResource(R.drawable.ruble),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
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
                    appInfoState.authorTelegramPage?.let { author ->
                        ColumnGroup(
                            items = listOf {
                                ClickableItem(
                                    title = author.name!!,
                                    subtitle = LocalContext.current.getString(R.string.author),
                                    leadingIcon = {
                                        LeadingAsyncImage(
                                            title = author.name,
                                            imageUrl = author.imageUrl!!
                                        )
                                    },
                                    onClick = {
                                        author.url?.let {
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