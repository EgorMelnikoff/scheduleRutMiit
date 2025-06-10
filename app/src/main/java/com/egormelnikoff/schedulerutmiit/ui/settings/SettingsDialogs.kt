package com.egormelnikoff.schedulerutmiit.ui.settings

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import androidx.core.net.toUri
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.repos.remote.parser.ParserRoutes.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.data.repos.remote.parser.ParserRoutes.APP_GITHUB_REPOS
import com.egormelnikoff.schedulerutmiit.data.repos.remote.parser.ParserRoutes.CLOUD_TIPS
import com.egormelnikoff.schedulerutmiit.ui.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.AppInfoState
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SchedulesState
import kotlinx.coroutines.launch

@Composable
fun SchedulesDialog(
    onShowDialog: (Boolean) -> Unit,
    navigateToSearch: () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    schedulesState: SchedulesState
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingsTopBar(
            title = LocalContext.current.getString(R.string.schedules),
            navAction = { onShowDialog(false) },
            navImageVector = Icons.AutoMirrored.Filled.ArrowBack
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        onShowDialog(false)
                        navigateToSearch()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }

        if (schedulesState is SchedulesState.Loaded) {
            val groupedSchedules = remember(schedulesState.savedSchedules) {
                schedulesState.savedSchedules
                    .groupBy { it.namedScheduleEntity.type }
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                groupedSchedules.forEach { schedules ->
                    stickyHeader {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background),
                            text = when (schedules.key) {
                                0 -> LocalContext.current.getString(R.string.Groups)
                                1 -> LocalContext.current.getString(R.string.Lecturers)
                                2 -> LocalContext.current.getString(R.string.Rooms)
                                else -> LocalContext.current.getString(R.string.schedules)
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    items(schedules.value) { schedule ->
                        Box(
                            modifier = Modifier.animateItem()
                        ) {
                            DialogScheduleItem(
                                scheduleViewModel = scheduleViewModel,
                                namedScheduleEntity = schedule.namedScheduleEntity,
                                onClick = { scheduleViewModel.selectNamedSchedule(schedule.namedScheduleEntity.id) }
                            )
                        }
                    }
                }
            }
        } else {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                buttonTitle = LocalContext.current.getString(R.string.search),
                imageVector = Icons.Default.Search,
                action = { navigateToSearch() }
            )
        }

    }
}


@Composable
fun DialogScheduleItem(
    scheduleViewModel: ScheduleViewModel,
    namedScheduleEntity: NamedScheduleEntity,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .defaultMinSize(minHeight = 52.dp)

    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = namedScheduleEntity.fullName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground
        )
        val scale by animateFloatAsState(
            targetValue = if (namedScheduleEntity.isDefault) 1f else 0f
        )
        if (namedScheduleEntity.isDefault) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale),
                imageVector = ImageVector.vectorResource(R.drawable.check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = {
                scheduleViewModel.deleteNamedSchedule(
                    primaryKey = namedScheduleEntity.id,
                    isDefault = namedScheduleEntity.isDefault
                )
            }
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun InfoDialog(
    appInfoState: AppInfoState,
    onShowDialog: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingsTopBar(
            title = LocalContext.current.getString(R.string.about_app),
            navAction = { onShowDialog(false) },
            navImageVector = Icons.AutoMirrored.Filled.ArrowBack
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = LocalContext.current.getString(R.string.app_name),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = LocalContext.current.getString(R.string.app_info),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                GroupSettingsItem {
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
                            val intent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    APP_CHANNEL_URL.toUri()
                                )
                            context.startActivity(intent)
                        }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 0.5.dp
                    )

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
                            val intent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    APP_GITHUB_REPOS.toUri()
                                )
                            context.startActivity(intent)
                        }
                    )
                }



                GroupSettingsItem {
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
                            val intent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    CLOUD_TIPS.toUri()
                                )
                            context.startActivity(intent)
                        }
                    )
                }

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
                            GroupSettingsItem {
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
                                        val intent =
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                appInfoState.authorTelegramPage.url?.toUri()
                                            )
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "${packageInfo.versionName} (${getLongVersionCode(packageInfo)})",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis
            )
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
            .background(MaterialTheme.colorScheme.surface)
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