package com.egormelnikoff.schedulerutmiit.ui.setting_screen.modal_dialog

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChipDefaults
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
import com.egormelnikoff.schedulerutmiit.core.common.AppConst.DEVELOPER
import com.egormelnikoff.schedulerutmiit.core.common.AppConst.DEVELOPER_EMAIL
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAssistChip
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.elements.RoundedBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoModalDialog(
    updatesAvailable: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(LocalContext.current.packageName, 0)
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current

    CustomModalBottomSheet(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
        showDragHandle = false,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
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

                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${packageInfo.versionName} (${getLongVersionCode(packageInfo)})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (updatesAvailable) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .background(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(4.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.alert),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
            }

            RoundedBox(
                title = stringResource(R.string.download_latest_release),
                shape = null,
                backgroundColor = Color.Unspecified
            ) {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        CustomAssistChip(
                            imageVector = ImageVector.vectorResource(R.drawable.logo_github),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                labelColor = MaterialTheme.colorScheme.onBackground,
                                leadingIconContentColor = Color.Unspecified
                            ),
                            isUnspecifiedIconColor = true,
                            border = BorderStroke(
                                color = MaterialTheme.colorScheme.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.github),
                            onClick = {
                                uriHandler.openUri(Endpoints.GITHUB_APP_LATEST_RELEASE)
                            }
                        )
                    }

                    item {
                        CustomAssistChip(
                            imageVector = ImageVector.vectorResource(R.drawable.logo_google_play),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                labelColor = MaterialTheme.colorScheme.onBackground,
                                leadingIconContentColor = Color.Unspecified
                            ),
                            isUnspecifiedIconColor = true,
                            border = BorderStroke(
                                color = MaterialTheme.colorScheme.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.google_play),
                            onClick = {
                                uriHandler.openUri(Endpoints.GOOGLE_PLAY)
                            }
                        )
                    }

                    item {
                        CustomAssistChip(
                            imageVector = ImageVector.vectorResource(R.drawable.logo_rustore),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                labelColor = MaterialTheme.colorScheme.onBackground,
                                leadingIconContentColor = Color.Unspecified
                            ),
                            isUnspecifiedIconColor = true,
                            border = BorderStroke(
                                color = MaterialTheme.colorScheme.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.rustore),
                            onClick = {
                                uriHandler.openUri(Endpoints.RU_STORE)
                            }
                        )
                    }
                }
            }

            RoundedBox(
                title = stringResource(R.string.contacts),
                shape = null,
                backgroundColor = Color.Unspecified
            ) {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        CustomAssistChip(
                            imageVector = ImageVector.vectorResource(R.drawable.logo_telegram),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                labelColor = MaterialTheme.colorScheme.onBackground,
                                leadingIconContentColor = Color.Unspecified
                            ),
                            isUnspecifiedIconColor = true,
                            border = BorderStroke(
                                color = MaterialTheme.colorScheme.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.telegram),
                            onClick = {
                                uriHandler.openUri(Endpoints.TG_APP_CHANNEL_URL)
                            }
                        )
                    }

                    item {
                        CustomAssistChip(
                            imageVector = ImageVector.vectorResource(R.drawable.email),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                labelColor = MaterialTheme.colorScheme.onBackground,
                                leadingIconContentColor = Color.Unspecified
                            ),
                            isUnspecifiedIconColor = true,
                            border = BorderStroke(
                                color = MaterialTheme.colorScheme.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.email),
                            onClick = {
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
                        )
                    }
                }
            }


            RoundedBox {
                ClickableItem(
                    title = DEVELOPER,
                    subtitle = stringResource(R.string.developer),
                    onLongClick = {
                        clipboard.nativeClipboard.setPrimaryClip(
                            ClipData.newPlainText(null, Endpoints.TG_AUTHOR_CHANNEL_URL)
                        )
                    }
                ) {
                    uriHandler.openUri(Endpoints.TG_AUTHOR_CHANNEL_URL)
                }
            }

            RoundedBox {
                ClickableItem(
                    title = stringResource(R.string.privacy_policy),
                    onLongClick = {
                        clipboard.nativeClipboard.setPrimaryClip(
                            ClipData.newPlainText(null, Endpoints.PRIVACY_POLICY)
                        )
                    }
                ) {
                    uriHandler.openUri(Endpoints.PRIVACY_POLICY)
                }
            }
        }
    }
}