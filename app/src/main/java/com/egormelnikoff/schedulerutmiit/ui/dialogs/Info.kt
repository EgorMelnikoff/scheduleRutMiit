package com.egormelnikoff.schedulerutmiit.ui.dialogs

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.AppConst.DEVELOPER
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.APP_GITHUB_LATEST_RELEASE_DOWNLOAD
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.APP_GITHUB_REPOS
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.AUTHOR_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.RU_STORE
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingTitle
import com.egormelnikoff.schedulerutmiit.ui.elements.RowGroup
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack

@Composable
fun InfoDialog(
    appBackStack: AppBackStack,
) {
    val packageInfo = LocalContext.current.packageManager.getPackageInfo(LocalContext.current.packageName, 0)
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = stringResource(R.string.about_app),
                navAction = { appBackStack.onBack() }
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
                    bottom = innerPadding.calculateBottomPadding()
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
                        text = stringResource(R.string.app_name),
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

            RowGroup(
                title = stringResource(R.string.download_latest_release),
                items = listOf(
                    {
                        ClickableItem(
                            title = stringResource(R.string.github),
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.logo_github),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            onLongClick = {
                                clipboard.nativeClipboard.setPrimaryClip(
                                    ClipData.newPlainText(null, APP_GITHUB_LATEST_RELEASE_DOWNLOAD)
                                )
                            }
                        ) {
                            uriHandler.openUri(APP_GITHUB_LATEST_RELEASE_DOWNLOAD)
                        }
                    }, {
                        ClickableItem(
                            title = stringResource(R.string.rustore),
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.logo_rustore)
                                )
                            },
                            onLongClick = {
                                clipboard.nativeClipboard.setPrimaryClip(
                                    ClipData.newPlainText(null, RU_STORE)
                                )
                            }
                        ) {
                            uriHandler.openUri(RU_STORE)
                        }
                    }
                )
            )

            ColumnGroup(
                items = listOf(
                    {
                        ClickableItem(
                            title = stringResource(R.string.telegram),
                            subtitle = stringResource(R.string.news_updates),
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.logo_telegram)
                                )
                            },
                            onLongClick = {
                                clipboard.nativeClipboard.setPrimaryClip(
                                    ClipData.newPlainText(null, APP_CHANNEL_URL)
                                )
                            }
                        ) {
                            uriHandler.openUri(APP_CHANNEL_URL)
                        }
                    }, {
                        ClickableItem(
                            title = stringResource(R.string.github),
                            subtitle = stringResource(R.string.source_code),
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.logo_github),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            onLongClick = {
                                clipboard.nativeClipboard.setPrimaryClip(
                                    ClipData.newPlainText(null, APP_GITHUB_REPOS)
                                )
                            }
                        ) {
                            uriHandler.openUri(APP_GITHUB_REPOS)
                        }
                    }
                )
            )

            ColumnGroup(
                items = listOf {
                    ClickableItem(
                        title = DEVELOPER,
                        subtitle = stringResource(R.string.developer),
                        leadingIcon = {
                            LeadingTitle(
                                title = DEVELOPER.first()
                            )
                        },
                        onLongClick = {
                            clipboard.nativeClipboard.setPrimaryClip(
                                ClipData.newPlainText(null, AUTHOR_CHANNEL_URL)
                            )
                        }
                    ) {
                        uriHandler.openUri(AUTHOR_CHANNEL_URL)
                    }
                }
            )
        }
    }
}