package com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.AppConst.DEVELOPER
import com.egormelnikoff.schedulerutmiit.app.AppConst.DEVELOPER_EMAIL
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.APP_GITHUB_LATEST_RELEASE_DOWNLOAD
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.APP_GITHUB_REPOS
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.AUTHOR_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.RU_STORE
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.ui.elements.RowGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoModalDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(LocalContext.current.packageName, 0)
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current

    CustomModalBottomSheet(
        modifier = Modifier.padding(start = 16.dp,end = 16.dp, top = 24.dp, bottom = 16.dp),
        showDragHandle = false,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                            showClickLabel = false,
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
                            showClickLabel = false,
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

            RowGroup(
                title = stringResource(R.string.report_a_problem),
                items = listOf(
                    {
                        ClickableItem(
                            title = stringResource(R.string.telegram),
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.logo_telegram)
                                )
                            },
                            showClickLabel = false,
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
                            title = stringResource(R.string.email),
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.email),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            showClickLabel = false,
                            onLongClick = {
                                clipboard.nativeClipboard.setPrimaryClip(
                                    ClipData.newPlainText(null, DEVELOPER_EMAIL)
                                )
                            }
                        ) {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:".toUri()
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(DEVELOPER_EMAIL))
                                putExtra(Intent.EXTRA_SUBJECT, "Сообщение о проблеме")
                            }

                            try {
                                context.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    "Приложение почты не найдено" + e.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            )


            ColumnGroup(
                items = listOf {
                    ClickableItem(
                        title = stringResource(R.string.github),
                        subtitle = stringResource(R.string.source_code),
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

            ColumnGroup(
                items = listOf {
                    ClickableItem(
                        title = DEVELOPER,
                        subtitle = stringResource(R.string.developer),
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