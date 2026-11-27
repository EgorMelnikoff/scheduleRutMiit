package com.egormelnikoff.schedulerutmiit.core.settings.ui.widget

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.core.common.consts.AppConst.DEVELOPER
import com.egormelnikoff.schedulerutmiit.core.common.consts.AppConst.DEVELOPER_EMAIL
import com.egormelnikoff.schedulerutmiit.core.common.consts.Links
import com.egormelnikoff.schedulerutmiit.core.settings.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.CenterContent
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.components.chip.AppAssistChip
import com.egormelnikoff.schedulerutmiit.core.ui.containers.RoundedBox
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

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

    AppBottomSheet(
        modifier = Modifier.padding(
            start = AppTheme.paddings.padding16,
            end = AppTheme.paddings.padding16,
            bottom = AppTheme.paddings.padding16
        ),
        showHandle = false,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding20)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding16)
            ) {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(84.dp),
                    imageVector = AppTheme.icons.logoApp,
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
                ) {
                    Text(
                        text = stringResource(R.string.settings_app_name),
                        style = AppTheme.typography.titleLarge,
                        color = AppTheme.colors.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(
                                    color = AppTheme.colors.primary,
                                    shape = AppTheme.shapes.small
                                )
                                .padding(
                                    horizontal = AppTheme.paddings.padding8,
                                    vertical = AppTheme.paddings.padding4
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${packageInfo.versionName} (${getLongVersionCode(packageInfo)})",
                                style = AppTheme.typography.titleMedium,
                                color = AppTheme.colors.onPrimary,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (updatesAvailable) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .background(
                                        color = AppTheme.colors.error,
                                        shape = AppTheme.shapes.small
                                    )
                                    .padding(AppTheme.paddings.padding4),
                                imageVector = AppTheme.icons.alert,
                                contentDescription = null,
                                tint = AppTheme.colors.onPrimary
                            )
                        }
                    }
                }
            }

            RoundedBox(
                title = stringResource(R.string.settings_download_latest_release),
                shape = null,
                backgroundColor = Color.Unspecified
            ) {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                ) {
                    item {
                        AppAssistChip(
                            imageVector = AppTheme.icons.logoGithub,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AppTheme.colors.background,
                                labelColor = AppTheme.colors.onBackground,
                                leadingIconContentColor = AppTheme.colors.onSecondaryContainer
                            ),
                            border = BorderStroke(
                                color = AppTheme.colors.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.settings_github),
                            onClick = {
                                uriHandler.openUri(Links.GITHUB_APP_LATEST_RELEASE)
                            }
                        )
                    }

                    item {
                        AppAssistChip(
                            imageVector = AppTheme.icons.logoGooglePlay,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AppTheme.colors.background,
                                labelColor = AppTheme.colors.onBackground,
                                leadingIconContentColor = Color.Unspecified
                            ),
                            isUnspecifiedIconColor = true,
                            border = BorderStroke(
                                color = AppTheme.colors.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.settings_google_play),
                            onClick = {
                                uriHandler.openUri(Links.GOOGLE_PLAY)
                            }
                        )
                    }

                    item {
                        AppAssistChip(
                            imageVector = AppTheme.icons.logoRustore,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AppTheme.colors.background,
                                labelColor = AppTheme.colors.onBackground,
                                leadingIconContentColor = Color.Unspecified
                            ),
                            isUnspecifiedIconColor = true,
                            border = BorderStroke(
                                color = AppTheme.colors.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.settings_rustore),
                            onClick = {
                                uriHandler.openUri(Links.RU_STORE)
                            }
                        )
                    }
                }
            }

            RoundedBox(
                title = stringResource(R.string.settings_contacts),
                shape = null,
                backgroundColor = Color.Unspecified
            ) {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                ) {
                    item {
                        AppAssistChip(
                            imageVector = AppTheme.icons.logoTelegram,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AppTheme.colors.background,
                                labelColor = AppTheme.colors.onBackground,
                                leadingIconContentColor = Color.Unspecified
                            ),
                            isUnspecifiedIconColor = true,
                            border = BorderStroke(
                                color = AppTheme.colors.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.settings_telegram),
                            onClick = {
                                uriHandler.openUri(Links.TG_APP_CHANNEL_URL)
                            }
                        )
                    }

                    item {
                        AppAssistChip(
                            imageVector = AppTheme.icons.email,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AppTheme.colors.background,
                                labelColor = AppTheme.colors.onBackground,
                                leadingIconContentColor = AppTheme.colors.onSecondaryContainer
                            ),
                            border = BorderStroke(
                                color = AppTheme.colors.outline,
                                width = 0.5.dp
                            ),
                            title = stringResource(R.string.settings_email),
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
                AppCell(
                    center = {
                        CenterContent(
                            title = {
                                TextBlock(
                                    text = DEVELOPER
                                )
                            },
                            subtitle = {
                                TextBlock(
                                    text = stringResource(R.string.settings_developer),
                                    typography = AppTheme.typography.bodyMedium,
                                    color = AppTheme.colors.onSecondaryContainer,
                                )
                            }
                        )
                    },
                    onLongClick = {
                        clipboard.nativeClipboard.setPrimaryClip(
                            ClipData.newPlainText(null, Links.TG_AUTHOR_CHANNEL_URL)
                        )
                    },
                    onClick = {
                        uriHandler.openUri(Links.TG_AUTHOR_CHANNEL_URL)
                    }
                )
            }

            RoundedBox {
                AppCell(
                    center = {
                        TextBlock(
                            text = stringResource(R.string.settings_privacy_policy),
                        )
                    },
                    onLongClick = {
                        clipboard.nativeClipboard.setPrimaryClip(
                            ClipData.newPlainText(null, Links.PRIVACY_POLICY)
                        )
                    },
                    onClick = {
                        uriHandler.openUri(Links.PRIVACY_POLICY)
                    }
                )
            }
        }
    }
}