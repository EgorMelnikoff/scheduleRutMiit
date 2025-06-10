package com.egormelnikoff.schedulerutmiit.ui.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.Event
import com.egormelnikoff.schedulerutmiit.data.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.settings.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeYellow
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun Event(
    showEventDialog: (Event?) -> Unit,
    events: List<Event>,
    eventsExtraData: List<EventExtraData>,
    isShortEvent: Boolean,
    isShowPriority: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (events.first().timeSlotName != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = events.first().timeSlotName.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text =
                    "${
                        events.first().startDatetime!!
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime()
                    } - ${
                        events.first().endDatetime!!
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime()
                    }",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            events.forEach { event ->
                SingleEvent(
                    eventExtraData = eventsExtraData.find {
                        it.id == event.id
                    },
                    isShortEvent = isShortEvent,
                    isShowPriority = isShowPriority,
                    event = event,
                    showEventDialog = showEventDialog
                )
            }
        }
    }
}

@Composable
fun SingleEvent(
    showEventDialog: (Event?) -> Unit,
    isShortEvent: Boolean,
    isShowPriority: Boolean,
    event: Event,
    eventExtraData: EventExtraData?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                onClick = { showEventDialog(event) },
            )
            .padding(bottom = 2.dp)
    ) {
        if (eventExtraData != null && isShowPriority) {
            val color = when (eventExtraData.tag) {
                1 -> lightThemeRed
                2 -> lightThemeOrange
                3 -> lightThemeYellow
                4 -> lightThemeGreen
                5 -> lightThemeLightBlue
                6 -> lightThemeBlue
                7 -> lightThemeViolet
                8 -> lightThemePink
                else -> Color.Unspecified
            }
            Canvas(Modifier.fillMaxWidth()) {
                val width = size.width
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = width, y = 0f),
                    color = color,
                    strokeWidth = 24f
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(
                    start = 12.dp, end = 12.dp,
                    top = 12.dp, bottom = 8.dp
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = event.typeName.toString(),
                fontSize = 12.sp,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = event.name.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!isShortEvent) {
                if (event.groups!!.isNotEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.group),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        event.groups.forEach { group ->
                            Text(
                                text = group.name!!,
                                fontSize = 12.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                if (event.rooms!!.isNotEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.room),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        event.rooms.forEach { room ->
                            Text(
                                text = room.name!!,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                if (event.lecturers!!.isNotEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.person),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        event.lecturers.forEach { lecturer ->
                            Text(
                                text = lecturer.shortFio!!,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
            if (eventExtraData != null && eventExtraData.comment != "") {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp
                )
                Comment(
                    message = eventExtraData.comment
                )
            }
        }
    }
}

@Composable
fun Comment(
    message: String,
    color: Color? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            modifier = Modifier
                .size(14.dp),
            imageVector = ImageVector.vectorResource(R.drawable.comment),
            contentDescription = null,
            tint = color ?: MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = message,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = color ?: MaterialTheme.colorScheme.onBackground
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    scheduleViewModel: ScheduleViewModel,
    apiId: String,
    scheduleId: Long,
    isSavedSchedule: Boolean,
    showEventDialog: (Event?) -> Unit,
    eventExtraData: EventExtraData?,
    event: Event
) {
    val context = LocalContext.current
    var tag by remember { mutableIntStateOf(eventExtraData?.tag ?: 0) }
    var comment by remember { mutableStateOf(eventExtraData?.comment ?: "") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = {
            showEventDialog(null)
            if (isSavedSchedule && eventExtraData?.comment != comment) {
                scheduleViewModel.updateEventExtra(
                    apiId = apiId,
                    scheduleId = scheduleId,
                    event = event,
                    comment = comment.trim(),
                    tag = tag
                )
            }
        },
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.outline
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(
                        text = event.startDatetime!!
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime().toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = event.endDatetime!!
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime().toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                VerticalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    thickness = 0.5.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = event.name!!,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = event.typeName!!,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp
            )
            if (!event.rooms.isNullOrEmpty()) {
                EventDialogItem(
                    title = context.getString(R.string.Room)
                ) {
                    event.rooms.forEach { room ->
                        EventDialogClickableItem(
                            text = room.hint.toString(),
                            onClick = {
                                showEventDialog(null)
                                scheduleViewModel.getSchedule(
                                    name = event.rooms.first().name!!,
                                    apiId = event.rooms.first().id.toString(),
                                    type = 2
                                )
                            }
                        )
                    }

                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp
                )
            }
            if (!event.groups.isNullOrEmpty()) {
                EventDialogItem(
                    title = context.getString(R.string.Groups)
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        event.groups.forEach { group ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .defaultMinSize(minWidth = 80.dp)
                                    .clickable(
                                        onClick = {
                                            showEventDialog(null)
                                            scheduleViewModel.getSchedule(
                                                name = group.name!!,
                                                apiId = group.id.toString(),
                                                type = 0
                                            )
                                        }
                                    )
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = group.name.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp
                )
            }
            if (!event.lecturers.isNullOrEmpty()) {
                EventDialogItem(
                    title = context.getString(R.string.Lecturers),
                ) {
                    event.lecturers.forEach { lecturer ->
                        EventDialogClickableItem(
                            text = lecturer.fullFio.toString(),
                            onClick = {
                                showEventDialog(null)
                                scheduleViewModel.getSchedule(
                                    name = lecturer.fullFio!!,
                                    apiId = lecturer.id.toString(),
                                    type = 1
                                )
                            },
                            imageUrl = "https://www.miit.ru/content/e${lecturer.id}.jpg?id_fe=${lecturer.id}&SWidth=100"
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp
                )
            }
            if (isSavedSchedule) {
                EventDialogItem(
                    title = LocalContext.current.getString(R.string.Tag)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ColorSelector(
                            currentTheme = "light",
                            currentSelected = tag,
                            onColorSelect = { value ->
                                tag = value
                                scheduleViewModel.updateEventExtra(
                                    apiId = apiId,
                                    scheduleId = scheduleId,
                                    event = event,
                                    comment = comment,
                                    tag = value
                                )
                            }
                        )
                        CommentField(
                            onCommentChanged = { newValue -> comment = newValue },
                            comment = comment
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun EventDialogItem(
    title: String,
    content: @Composable () -> Unit,
) {
    Column (
        modifier = Modifier.padding(top = 8.dp)
    ){
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content.invoke()
    }
}


@Composable
fun EventDialogClickableItem(
    onClick: (() -> Unit),
    text: String,
    imageUrl: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (imageUrl != null) {
            val model = rememberAsyncImagePainter(imageUrl)
            val transition by animateFloatAsState(
                targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
            )
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .alpha(transition),
                    contentScale = ContentScale.Crop,
                    painter = model,
                    contentDescription = null,
                )
                when (model.state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    is AsyncImagePainter.State.Error, AsyncImagePainter.State.Empty -> {
                        Text(
                            text = text.first().toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    else -> {

                    }
                }
            }
        }
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun CommentField(
    onCommentChanged: (String) -> Unit,
    comment: String
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        value = comment,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,

            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
        ),
        placeholder = {
            Text(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                text = LocalContext.current.getString(R.string.Enter_comment)
            )
        },
        onValueChange = { newQuery ->
            onCommentChanged(newQuery)
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false, imeAction = ImeAction.Done
        ),
        trailingIcon = {
            AnimatedVisibility(
                visible = comment != "",
                enter = scaleIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                IconButton(
                    onClick = {
                        onCommentChanged("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
            }
        ),
    )
}