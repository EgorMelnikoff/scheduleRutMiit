package com.egormelnikoff.schedulerutmiit.ui.dialogs

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun EventDialog(
    externalPadding: PaddingValues,
    onBack: () -> Unit,
    onSearchNamedSchedule: (Triple<String, String, Int>) -> Unit,
    onEventExtraChange: (Pair<String, Int>) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onHideEvent: (Long) -> Unit,
    onShowEvent: (Long) -> Unit,
    event: Event,
    eventExtraData: EventExtraData?,
    isSavedSchedule: Boolean,
    isCustomSchedule: Boolean,
) {
    var showEventActionsDialog by remember { mutableStateOf(false) }
    var showEventDeleteDialog by remember { mutableStateOf(false) }
    var showEventHideDialog by remember { mutableStateOf(false) }

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
            CustomTopAppBar(
                navAction = {
                    onBack()
                }
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
                if (isSavedSchedule) {
                    IconButton(
                        onClick = {
                            showEventActionsDialog = true
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.more_vert),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = externalPadding.calculateBottomPadding(),
                    start = 16.dp, end = 16.dp
                )
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
            if (!event.rooms.isNullOrEmpty()) {
                ColumnGroup(
                    title = context.getString(R.string.Room),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = event.rooms.map { room ->
                        {
                            ClickableItem(
                                title = room.hint.toString(),
                                onClick = if (!isCustomSchedule) {
                                    {
                                        onBack()
                                        onSearchNamedSchedule(
                                            Triple(
                                                room.name!!,
                                                room.id.toString(),
                                                2
                                            )
                                        )
                                    }
                                } else null
                            )
                        }
                    }

                )
            }
            if (!event.groups.isNullOrEmpty() && !isCustomSchedule) {
                ColumnGroup(
                    title = context.getString(R.string.Groups),
                    titleColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = Color.Unspecified,
                    items = listOf {
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
                                                onSearchNamedSchedule(
                                                    Triple(
                                                        group.name!!,
                                                        group.id.toString(),
                                                        0
                                                    )
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
                )
            }
            if (!event.lecturers.isNullOrEmpty()) {
                ColumnGroup(
                    title = context.getString(R.string.Lecturers),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = event.lecturers.map { lecturer ->
                        {
                            ClickableItem(
                                title = lecturer.fullFio.toString(),
                                onClick = if (!isCustomSchedule) {
                                    {
                                        onBack()
                                        onSearchNamedSchedule(
                                            Triple(
                                                lecturer.fullFio!!,
                                                lecturer.id.toString(),
                                                1
                                            )
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
                ColumnGroup(
                    title = LocalContext.current.getString(R.string.comment),
                    titleColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = Color.Unspecified,
                    items = listOf {
                        CustomTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            value = comment,
                            onValueChanged = { newValue ->
                                comment = newValue
                                onEventExtraChange(Pair(newValue, tag))
                            },
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
                                            comment = ""
                                            onEventExtraChange(Pair("", tag))
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
                )
                ColumnGroup(
                    title = LocalContext.current.getString(R.string.Tag),
                    titleColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = Color.Unspecified,
                    items = listOf {
                        ColorSelector(
                            currentSelected = tag,
                            onColorSelect = { value ->
                                tag = value
                                onEventExtraChange(Pair(comment, value))
                            }
                        )
                    }
                )
            }
        }
    }
    if (showEventDeleteDialog) {
        CustomAlertDialog(
            dialogIcon = ImageVector.vectorResource(R.drawable.delete),
            dialogTitle = "${LocalContext.current.getString(R.string.delete_event)}?",
            dialogText = LocalContext.current.getString(R.string.event_deleting_alert),
            onDismissRequest = {
                showEventDeleteDialog = false
            },
            onConfirmation = {
                onDeleteEvent(event.id)
            }
        )
    }
    if (showEventHideDialog) {
        CustomAlertDialog(
            dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
            dialogTitle = "${LocalContext.current.getString(R.string.hide_event)}?",
            dialogText = LocalContext.current.getString(R.string.event_visibility_alert),
            onDismissRequest = {
                showEventHideDialog = false
            },
            onConfirmation = {
                onHideEvent(event.id)
            }
        )
    }
    if (showEventActionsDialog) {
        DialogEventActions(
            event = event,
            onDismiss = {
                showEventActionsDialog = false
            },
            onDeleteEvent = if (event.isCustomEvent) {
                {
                    showEventDeleteDialog = true
                    onBack()
                }
            } else null,
            onHideEvent = if (!event.isHidden) {
                {
                    showEventHideDialog = true
                    event.isHidden = true

                }
            } else null,
            onShowEvent = if (event.isHidden) {
                {
                    onShowEvent(event.id)
                    event.isHidden = false
                }
            } else null
        )
    }
}