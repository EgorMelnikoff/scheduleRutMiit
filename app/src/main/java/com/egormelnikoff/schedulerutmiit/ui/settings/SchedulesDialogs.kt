package com.egormelnikoff.schedulerutmiit.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.composable.SimpleTopBar
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModel


@Composable
fun SchedulesDialog(
    onBack: () -> Unit,
    navigateToSearch: () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    scheduleUiState: ScheduleUiState,
    paddingValues: PaddingValues
) {
    Scaffold(
        topBar = {
            SimpleTopBar(
                title = LocalContext.current.getString(R.string.schedules),
                navAction = { onBack() },
                navImageVector = ImageVector.vectorResource(R.drawable.back)
            ) {
                IconButton(
                    onClick = {
                        navigateToSearch()
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                }
            }
        }
    ) { padding ->
        if (scheduleUiState.savedNamedSchedules.isNotEmpty()) {
            val groupedSchedules = remember(scheduleUiState.savedNamedSchedules) {
                scheduleUiState.savedNamedSchedules
                    .sortedBy { it.type }
                    .groupBy { it.type }

            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
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
                    items(schedules.value) { namedScheduleEntity ->
                        DialogScheduleItem(
                            scheduleViewModel = scheduleViewModel,
                            namedScheduleEntity = namedScheduleEntity
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
                paddingBottom = 0.dp
            )
        }
    }
}

@Composable
fun DialogScheduleItem(
    scheduleViewModel: ScheduleViewModel,
    namedScheduleEntity: NamedScheduleEntity
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )
    val scale by animateFloatAsState(
        targetValue = if (namedScheduleEntity.isDefault) 1f else 0f
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .let {
                if (namedScheduleEntity.isDefault) {
                    it
                } else {
                    it.clickable(onClick = { isExpanded = !isExpanded })
                }
            }
            .padding(top = 12.dp, bottom = 6.dp, start = 12.dp, end = 12.dp)
            .defaultMinSize(minHeight = 52.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = namedScheduleEntity.fullName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground
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
                    if (namedScheduleEntity.isDefault) {
                        scheduleViewModel.deleteNamedSchedule(
                            primaryKey = namedScheduleEntity.id,
                            isDefault = true
                        )
                    } else {
                        isExpanded = !isExpanded
                    }

                }
            ) {
                if (namedScheduleEntity.isDefault) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                } else {
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
            visible = !namedScheduleEntity.isDefault && isExpanded
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
                            primaryKey = namedScheduleEntity.id,
                            isDefault = namedScheduleEntity.isDefault
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
                    visible = !namedScheduleEntity.isDefault,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    ScheduleButton(
                        modifier = Modifier,
                        onClick = {
                            isExpanded = false
                            scheduleViewModel.selectDefaultNamedSchedule(namedScheduleEntity.id)
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

