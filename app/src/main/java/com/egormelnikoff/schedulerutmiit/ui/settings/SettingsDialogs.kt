package com.egormelnikoff.schedulerutmiit.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.APP_GITHUB_REPOS
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.CLOUD_TIPS
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.ui.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.SchedulesState
import com.egormelnikoff.schedulerutmiit.ui.settings.viewmodel.AppInfoState


@Composable
fun SchedulesDialog(
    onShowDialog: (Boolean) -> Unit,
    navigateToSearch: () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    schedulesState: SchedulesState,
    //showDialogAddSchedule: (Boolean) -> Unit,
    //onShowDialogAddEvent: (Long?) -> Unit,
    paddingBottom: Dp,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingsTopBar(
            title = LocalContext.current.getString(R.string.schedules),
            navAction = { onShowDialog(false) },
            navImageVector = ImageVector.vectorResource(R.drawable.back)
        ) {
            IconButton(
                onClick = {
                    onShowDialog(false)
                    navigateToSearch()
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                    contentDescription = null
                )
            }
        }

        if (schedulesState is SchedulesState.Loaded) {
            val groupedSchedules = remember(schedulesState.savedSchedules) {
                schedulesState.savedSchedules
                    .sortedBy { it.namedScheduleEntity.type }
                    .groupBy { it.namedScheduleEntity.type }

            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = paddingBottom)
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
                                3 -> LocalContext.current.getString(R.string.my)
                                else -> LocalContext.current.getString(R.string.schedules)
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    items(schedules.value) { schedule ->
                        DialogScheduleItem(
                            scheduleViewModel = scheduleViewModel,
                            namedScheduleFormatted = schedule,
                            //onShowDialogAddEvent = onShowDialogAddEvent
                        )
                    }
                }
            }
        } else {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                buttonTitle = LocalContext.current.getString(R.string.search),
                imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                action = { navigateToSearch() },
                paddingBottom = paddingBottom
            )
        }

    }
}

@Composable
fun DialogScheduleItem(
    scheduleViewModel: ScheduleViewModel,
    namedScheduleFormatted: NamedScheduleFormatted,
    //onShowDialogAddEvent: (Long?) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .let {
                if (namedScheduleFormatted.namedScheduleEntity.isDefault) {
                    it
                } else {
                    it.clickable(onClick = { isExpanded = !isExpanded })
                }
            }
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .defaultMinSize(minHeight = 52.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = namedScheduleFormatted.namedScheduleEntity.fullName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground
            )
            val scale by animateFloatAsState(
                targetValue = if (namedScheduleFormatted.namedScheduleEntity.isDefault) 1f else 0f
            )
            if (namedScheduleFormatted.namedScheduleEntity.isDefault) {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale),
                    imageVector = ImageVector.vectorResource(R.drawable.check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (namedScheduleFormatted.namedScheduleEntity.isDefault) {
                IconButton(
                    onClick = {
                        scheduleViewModel.deleteNamedSchedule(
                            primaryKey = namedScheduleFormatted.namedScheduleEntity.id,
                            isDefault = namedScheduleFormatted.namedScheduleEntity.isDefault
                        )
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        isExpanded = !isExpanded
                    }
                ) {
                    Icon(
                        modifier = Modifier.graphicsLayer(
                            rotationZ = rotationAngle
                        ),
                        imageVector = ImageVector.vectorResource(R.drawable.down),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                }
            }

        }

        AnimatedVisibility(
            visible = !namedScheduleFormatted.namedScheduleEntity.isDefault && isExpanded
        ) {
            FlowRow(
                maxItemsInEachRow = 2,
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScheduleButton(
                    modifier = Modifier,
                    onClick = {
                        scheduleViewModel.deleteNamedSchedule(
                            primaryKey = namedScheduleFormatted.namedScheduleEntity.id,
                            isDefault = namedScheduleFormatted.namedScheduleEntity.isDefault
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    title = LocalContext.current.getString(R.string.delete),
                    borderStroke = BorderStroke(
                        width = 0.5.dp,
                        MaterialTheme.colorScheme.error
                    ),
                    imageVector = ImageVector.vectorResource(R.drawable.delete)
                )
                AnimatedVisibility(
                    visible = !namedScheduleFormatted.namedScheduleEntity.isDefault,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    ScheduleButton(
                        modifier = Modifier,
                        onClick = {
                            isExpanded = false
                            scheduleViewModel.selectNamedSchedule(namedScheduleFormatted.namedScheduleEntity.id)
                        },
                        colors = ButtonDefaults.outlinedButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        title = LocalContext.current.getString(R.string.make_default),
                        borderStroke = BorderStroke(
                            width = 0.5.dp,
                            MaterialTheme.colorScheme.onBackground
                        ),
                        imageVector = null
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleButton(
    onClick: () -> Unit,
    imageVector: ImageVector?,
    title: String,
    colors: ButtonColors,
    borderStroke: BorderStroke?,
    modifier: Modifier
) {
    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = colors,
        border = borderStroke,
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (imageVector != null) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = imageVector,
                    contentDescription = null
                )
            }
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun InfoDialog(
    appInfoState: AppInfoState,
    onShowDialog: (Boolean) -> Unit,
    paddingBottom: Dp
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingsTopBar(
            title = LocalContext.current.getString(R.string.about_app),
            navAction = { onShowDialog(false) },
            navImageVector = ImageVector.vectorResource(R.drawable.back)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
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
                        uriHandler.openUri(APP_CHANNEL_URL)
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
                        uriHandler.openUri(APP_GITHUB_REPOS)
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
                        uriHandler.openUri(CLOUD_TIPS)
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
                                    appInfoState.authorTelegramPage.url?.let {
                                        uriHandler.openUri(
                                            it
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(paddingBottom)
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
