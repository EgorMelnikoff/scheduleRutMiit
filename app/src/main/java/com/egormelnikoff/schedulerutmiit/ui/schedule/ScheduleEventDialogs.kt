package com.egormelnikoff.schedulerutmiit.ui.schedule

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.egormelnikoff.schedulerutmiit.ui.settings.SettingsTopBar
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun EventDialog(
    scheduleViewModel: ScheduleViewModel,
    namedScheduleId: Long,
    scheduleId: Long,
    isSavedSchedule: Boolean,
    onShowEventDialog: (Boolean) -> Unit,
    eventExtraData: EventExtraData?,
    event: Event
) {
    val context = LocalContext.current
    var tag by remember { mutableIntStateOf(eventExtraData?.tag ?: 0) }
    var comment by remember { mutableStateOf(eventExtraData?.comment ?: "") }

    val startTime = "${
        event.startDatetime!!
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalTime()
    }"
    val endTime = "${
        event.endDatetime!!
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalTime()
    }"

    val eventString = StringBuilder().apply {
        append("${LocalContext.current.getString(R.string.Class)}: ${event.name}")
        append("\n${LocalContext.current.getString(R.string.type)}: ${event.typeName}")
        append("\n${LocalContext.current.getString(R.string.time)}: $startTime - $endTime")

        if (event.timeSlotName != null) {
            append(" (${event.timeSlotName})")
        }

        if (!event.rooms.isNullOrEmpty()) {
            append("\n${LocalContext.current.getString(R.string.place)}: ${event.rooms.joinToString { it.name.toString() }}")
        }

        if (!event.lecturers.isNullOrEmpty()) {
            append("\n${LocalContext.current.getString(R.string.Lecturers)}: ${event.lecturers.joinToString { it.shortFio.toString() }}")
        }

        if (!event.groups.isNullOrEmpty()) {
            append("\n${LocalContext.current.getString(R.string.groups)}: ${event.groups.joinToString { it.name.toString() }}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SettingsTopBar(
            title = "",
            navAction = {
                onShowEventDialog(false)
            },
            navImageVector = ImageVector.vectorResource(R.drawable.back)
        ) {
            IconButton(
                onClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, eventString.toString())
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.share),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Column (
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = event.name!!,
                    fontSize = 20.sp,
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$startTime - $endTime",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (!event.rooms.isNullOrEmpty()) {
                EventDialogItem(
                    title = context.getString(R.string.Room),
                ) {
                    Column {
                        event.rooms.forEachIndexed { index, room ->
                            EventDialogClickableItem(
                                text = room.hint.toString(),
                                onClick = {
                                    onShowEventDialog(false)
                                    scheduleViewModel.getSchedule(
                                        name = event.rooms.first().name!!,
                                        apiId = event.rooms.first().id.toString(),
                                        type = 2
                                    )
                                }
                            )
                            if (index != event.rooms.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    color = MaterialTheme.colorScheme.outline,
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }

            }
            if (!event.groups.isNullOrEmpty()) {
                EventDialogItem(
                    title = context.getString(R.string.Groups),
                    withBackground = false
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                                            onShowEventDialog(false)
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
            }
            if (!event.lecturers.isNullOrEmpty()) {
                EventDialogItem(
                    title = context.getString(R.string.Lecturers),
                ) {
                    Column {
                        event.lecturers.forEachIndexed { index, lecturer ->
                            EventDialogClickableItem(
                                text = lecturer.fullFio.toString(),
                                onClick = {
                                    onShowEventDialog(false)
                                    scheduleViewModel.getSchedule(
                                        name = lecturer.fullFio!!,
                                        apiId = lecturer.id.toString(),
                                        type = 1
                                    )
                                },
                                imageUrl = "https://www.miit.ru/content/e${lecturer.id}.jpg?id_fe=${lecturer.id}&SWidth=100"
                            )
                            if (index != event.lecturers.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    color = MaterialTheme.colorScheme.outline,
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }

                }
            }
            if (isSavedSchedule) {
                EventDialogItem(
                    title = LocalContext.current.getString(R.string.comment),
                    withBackground = false
                ) {
                    CommentField(
                        onCommentChanged = { newValue ->
                            comment = newValue
                            scheduleViewModel.updateEventExtra(
                                namedScheduleId,
                                scheduleId,
                                event,
                                comment,
                                tag
                            )
                        },
                        comment = comment
                    )
                }
                EventDialogItem(
                    title = LocalContext.current.getString(R.string.Tag),
                    withBackground = false
                ) {
                    ColorSelector(
                        currentSelected = tag,
                        onColorSelect = { value ->
                            tag = value
                            scheduleViewModel.updateEventExtra(
                                namedScheduleId = namedScheduleId,
                                scheduleId = scheduleId,
                                event = event,
                                comment = comment,
                                tag = value
                            )
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun EventDialogItem(
    title: String,
    withBackground: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (withBackground) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                content.invoke()
            }
        } else {
            content.invoke()
        }
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
            .padding(12.dp),
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
                        .let {
                            if (model.state !is  AsyncImagePainter.State.Success) {
                                it .border(0.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            } else it
                        }
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
                        imageVector = ImageVector.vectorResource(R.drawable.clear),
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

