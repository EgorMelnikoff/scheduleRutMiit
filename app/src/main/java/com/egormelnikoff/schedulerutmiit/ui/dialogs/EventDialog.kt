package com.egormelnikoff.schedulerutmiit.ui.dialogs

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.SimpleTopBar
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun EventDialog(
    onBack: () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    event: Event,
    eventExtraData: EventExtraData?,
    isSavedSchedule: Boolean,
    isCustomSchedule: Boolean
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
        if (event.typeName != null) {
            append("\n${LocalContext.current.getString(R.string.class_type)}: ${event.typeName}")
        }
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
    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "",
                navAction = {
                    onBack()
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding(), start = 16.dp, end = 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
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
            if (event.typeName != null) {
                Text(
                    text = event.typeName,
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
            }
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
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            if (!event.rooms.isNullOrEmpty()) {
                ColumnGroup(
                    title = context.getString(R.string.Room),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = event.rooms.map { room ->
                        {
                            EventDialogItemContent(
                                text = room.hint.toString(),
                                onClick = if (!isCustomSchedule) {
                                    {
                                        onBack()
                                        scheduleViewModel.getNamedScheduleFromApi(
                                            name = event.rooms.first().name!!,
                                            apiId = event.rooms.first().id.toString(),
                                            type = 2
                                        )
                                    }
                                } else null
                            )
                        }
                    }

                )
            }
            if (!event.groups.isNullOrEmpty() && !isCustomSchedule) {
                EventDialogItem(
                    title = context.getString(R.string.Groups),
                    titleColor = MaterialTheme.colorScheme.primary,
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
                                            onBack()
                                            scheduleViewModel.getNamedScheduleFromApi(
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
                ColumnGroup(
                    title = context.getString(R.string.Lecturers),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = event.lecturers.map { lecturer ->
                        {
                            EventDialogItemContent(
                                text = lecturer.fullFio.toString(),
                                onClick = if (!isCustomSchedule) {
                                    {
                                        onBack()
                                        scheduleViewModel.getNamedScheduleFromApi(
                                            name = lecturer.fullFio!!,
                                            apiId = lecturer.id.toString(),
                                            type = 1
                                        )
                                    }
                                } else null,
                                imageUrl = if (lecturer.id != null && lecturer.url != null) "https://www.miit.ru/content/e${lecturer.id}.jpg?id_fe=${lecturer.id}&SWidth=100" else null
                            )
                        }
                    }
                )
            }
            if (isSavedSchedule) {
                val onCommentChange: (String) -> Unit = { newValue ->
                    comment = newValue
                    scheduleViewModel.updateEventExtra(
                        event,
                        comment,
                        tag
                    )
                }
                EventDialogItem(
                    title = LocalContext.current.getString(R.string.comment),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false
                ) {
                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        value = comment,
                        onValueChanged = onCommentChange,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Default
                        ),
                        placeholderText = LocalContext.current.getString(R.string.Enter_comment),
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = comment != "",
                                enter = scaleIn(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(500))
                            ) {
                                IconButton(
                                    onClick = {
                                        onCommentChange("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.clear),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    )
                }
                EventDialogItem(
                    title = LocalContext.current.getString(R.string.Tag),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false
                ) {
                    ColorSelector(
                        currentSelected = tag,
                        onColorSelect = { value ->
                            tag = value
                            scheduleViewModel.updateEventExtra(
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
    titleColor: Color? = null,
    withBackground: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor ?: MaterialTheme.colorScheme.onSurface
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
fun EventDialogItemContent(
    onClick: (() -> Unit)? = null,
    text: String,
    imageUrl: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (onClick != null) {
                    it.clickable { onClick() }
                } else {
                    it
                }
            }
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
                            if (model.state !is AsyncImagePainter.State.Success) {
                                it.border(0.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
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
        if (onClick != null) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}