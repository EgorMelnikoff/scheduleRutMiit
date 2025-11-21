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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.EventTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingTitle
import com.egormelnikoff.schedulerutmiit.ui.elements.ModalDialogEvent
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    event: Event,
    eventExtraData: EventExtraData?,
    scheduleEntity: ScheduleEntity,
    isSavedSchedule: Boolean,
    isCustomSchedule: Boolean,
    navigationActions: NavigationActions,
    scheduleActions: ScheduleActions,
    externalPadding: PaddingValues
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var showEventActionsDialog by remember { mutableStateOf(false) }
    var showEventDeleteDialog by remember { mutableStateOf(false) }
    var showEventHideDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var tag by remember { mutableIntStateOf(eventExtraData?.tag ?: 0) }
    var comment by remember { mutableStateOf(eventExtraData?.comment ?: "") }

    val startTime = "${
        event.startDatetime!!.toLocaleTimeWithTimeZone()
    }"
    val endTime = "${
        event.endDatetime!!.toLocaleTimeWithTimeZone()
    }"

    val subtitle = StringBuilder().apply {
        append("$startTime - $endTime")
        event.typeName?.let {
            append("  |  ")
            append(it)
        }

    }.toString()

    LaunchedEffect(comment, tag) {
        delay(300)
        scheduleActions.eventActions.onEventExtraChange(Triple(event, comment, tag))
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EventTopAppBar(
                title = event.name!!,
                subtitle = subtitle,
                scrollBehavior = scrollBehavior,
                navAction = {
                    navigationActions.onBack()
                },
                actions = {
                    IconButton(
                        onClick = {
                            val eventString = StringBuilder().apply {
                                append("${context.getString(R.string._class)}: ${event.name}")
                                event.typeName?.let {
                                    append("\n${context.getString(R.string.class_type)}: $it")
                                }
                                append("\n${context.getString(R.string.time)}: $startTime - $endTime")

                                event.timeSlotName?.let {
                                    append(" ($it)")
                                }

                                if (!event.rooms.isNullOrEmpty()) {
                                    append("\n${context.getString(R.string.place)}: ${event.rooms.joinToString { it.name.toString() }}")
                                }

                                if (!event.lecturers.isNullOrEmpty()) {
                                    append("\n${context.getString(R.string.lecturers)}: ${event.lecturers.joinToString { it.shortFio.toString() }}")
                                }

                                if (!event.groups.isNullOrEmpty()) {
                                    append("\n${context.getString(R.string.groups)}: ${event.groups.joinToString { it.name.toString() }}")
                                }
                            }
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
                            modifier = Modifier.size(24.dp),
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
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.more_vert),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            )
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
            if (!event.groups.isNullOrEmpty()) {
                ColumnGroup(
                    title = context.getString(R.string.groups),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false,
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
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .defaultMinSize(minWidth = 80.dp)
                                        .let {
                                            if (!isCustomSchedule) {
                                                it.clickable(
                                                    onClick = {
                                                        navigationActions.navigateToSchedule()
                                                        scheduleActions.onGetNamedSchedule(
                                                            Triple(
                                                                group.name!!,
                                                                group.id.toString(),
                                                                0
                                                            )
                                                        )
                                                    }
                                                )
                                            } else it
                                        }
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = group.name.toString(),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                )
            }
            if (!event.rooms.isNullOrEmpty()) {
                ColumnGroup(
                    title = context.getString(R.string.room),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = event.rooms.map { room ->
                        {
                            ClickableItem(
                                title = room.hint.toString(),
                                titleMaxLines = 2,
                                defaultMinHeight = 32.dp,
                                onClick = if (!isCustomSchedule) {
                                    {
                                        navigationActions.navigateToSchedule()
                                        scheduleActions.onGetNamedSchedule(
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
            if (!event.lecturers.isNullOrEmpty()) {
                ColumnGroup(
                    title = context.getString(R.string.lecturers),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = event.lecturers.map { lecturer ->
                        {
                            ClickableItem(
                                title = lecturer.fullFio.toString(),
                                titleMaxLines = 2,
                                defaultMinHeight = 32.dp,
                                onClick = if (!isCustomSchedule) {
                                    {
                                        navigationActions.navigateToSchedule()
                                        scheduleActions.onGetNamedSchedule(
                                            Triple(
                                                lecturer.fullFio!!,
                                                lecturer.id.toString(),
                                                1
                                            )
                                        )
                                    }
                                } else null,
                                leadingIcon = {
                                    LeadingTitle(
                                        title = lecturer.fullFio.toString()
                                    )
                                }
                            )
                        }
                    }
                )
            }
            if (isSavedSchedule) {
                ColumnGroup(
                    title = context.getString(R.string.comment),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false,
                    items = listOf {
                        CustomTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            value = comment,
                            onValueChanged = { newValue ->
                                comment = newValue
                            },
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                imeAction = ImeAction.Default
                            ),
                            maxSymbols = 100,
                            placeholderText = context.getString(R.string.enter_comment),
                            trailingIcon = {
                                AnimatedVisibility(
                                    visible = comment != "",
                                    enter = scaleIn(animationSpec = tween(300)),
                                    exit = fadeOut(animationSpec = tween(500))
                                ) {
                                    IconButton(
                                        onClick = {
                                            comment = ""
                                            scheduleActions.eventActions.onEventExtraChange(
                                                Triple(
                                                    event,
                                                    "",
                                                    tag
                                                )
                                            )
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
                    title = context.getString(R.string.tag),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false,
                    items = listOf {
                        ColorSelector(
                            currentSelected = tag,
                            onColorSelect = { value ->
                                tag = value
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
            dialogTitle = "${context.getString(R.string.delete_event)}?",
            dialogText = context.getString(R.string.event_deleting_alert),
            onDismissRequest = {
                showEventDeleteDialog = false
            },
            onConfirmation = {
                scheduleActions.eventActions.onDeleteEvent(Pair(scheduleEntity, event.id))
                navigationActions.onBack()
            }
        )
    }
    if (showEventHideDialog) {
        CustomAlertDialog(
            dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
            dialogTitle = "${context.getString(R.string.hide_event)}?",
            dialogText = context.getString(R.string.event_visibility_alert),
            onDismissRequest = {
                showEventHideDialog = false
            },
            onConfirmation = {
                scheduleActions.eventActions.onHideEvent(Pair(scheduleEntity, event.id))
                navigationActions.onBack()
            }
        )
    }
    if (showEventActionsDialog) {
        ModalDialogEvent(
            event = event,
            onDismiss = {
                showEventActionsDialog = false
            },
            onDeleteEvent = if (event.isCustomEvent) {
                {
                    showEventDeleteDialog = true
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
                    scheduleActions.eventActions.onShowEvent(Pair(scheduleEntity, event.id))
                    event.isHidden = false
                }
            } else null
        )
    }
}