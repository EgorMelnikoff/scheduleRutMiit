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
import androidx.compose.ui.res.stringResource
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
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    appUiState: AppUiState,
    event: Event,
    eventExtraData: EventExtraData?,
    scheduleEntity: ScheduleEntity,
    isSavedSchedule: Boolean,
    navigationActions: NavigationActions,
    scheduleActions: ScheduleActions,
    externalPadding: PaddingValues
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var showEventActionsDialog by remember { mutableStateOf(false) }
    var showEventDeleteDialog by remember { mutableStateOf(false) }
    var showEventHideDialog by remember { mutableStateOf(false) }

    var tag by remember { mutableIntStateOf(eventExtraData?.tag ?: 0) }
    var comment by remember { mutableStateOf(eventExtraData?.comment ?: "") }

    val subtitle = StringBuilder().apply {
        append("${event.startDatetime!!.toLocaleTimeWithTimeZone()} - ${event.endDatetime!!.toLocaleTimeWithTimeZone()}")
        event.typeName?.let {
            append("  |  ")
            append(it)
        }
    }.toString()

    if (isSavedSchedule) {
        LaunchedEffect(comment, tag) {
            delay(300)
            scheduleActions.eventActions.onEventExtraChange(event, comment, tag)
        }
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
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    event.customToString(appUiState.context)
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            appUiState.context.startActivity(shareIntent)
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.share),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(
                        onClick = {
                            var eventString = event.customToString(appUiState.context)
                            if (comment.isNotEmpty()) {
                                eventString += "\n${appUiState.context.getString(R.string.comment)}: $comment"
                            }
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, eventString)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            appUiState.context.startActivity(shareIntent)
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.share_with_comment),
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
                    title = stringResource(R.string.groups),
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
                                            if (!event.isCustomEvent) {
                                                it.clickable(
                                                    onClick = {
                                                        navigationActions.navigateToSchedule()
                                                        scheduleActions.onGetNamedSchedule(
                                                            group.name!!,
                                                            group.id.toString(),
                                                            0
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
                    title = stringResource(R.string.room),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = event.rooms.map { room ->
                        {
                            ClickableItem(
                                title = room.hint.toString(),
                                titleMaxLines = 2,
                                defaultMinHeight = 32.dp,
                                onClick = if (!event.isCustomEvent) {
                                    {
                                        navigationActions.navigateToSchedule()
                                        scheduleActions.onGetNamedSchedule(
                                            room.name!!,
                                            room.id.toString(),
                                            2
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
                    title = stringResource(R.string.lecturers),
                    titleColor = MaterialTheme.colorScheme.primary,
                    items = event.lecturers.map { lecturer ->
                        {
                            ClickableItem(
                                title = lecturer.fullFio.toString(),
                                titleMaxLines = 2,
                                defaultMinHeight = 32.dp,
                                onClick = if (!event.isCustomEvent) {
                                    {
                                        navigationActions.navigateToSchedule()
                                        scheduleActions.onGetNamedSchedule(
                                            lecturer.fullFio!!,
                                            lecturer.id.toString(),
                                            1
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
                    title = stringResource(R.string.comment),
                    titleColor = MaterialTheme.colorScheme.primary,
                    withBackground = false,
                    items = listOf {
                        CustomTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            value = comment,
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                imeAction = ImeAction.Default
                            ),
                            maxSymbols = 100,
                            placeholderText = stringResource(R.string.enter_comment),
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
                                                event,
                                                "",
                                                tag
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
                        ) { newValue ->
                            comment = newValue
                        }
                    }
                )
                ColumnGroup(
                    title = stringResource(R.string.tag),
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
            dialogTitle = "${stringResource(R.string.delete_event)}?",
            dialogText = stringResource(R.string.event_deleting_alert),
            onDismissRequest = {
                showEventDeleteDialog = false
            },
            onConfirmation = {
                scheduleActions.eventActions.onDeleteEvent(scheduleEntity, event.id)
                navigationActions.onBack()
            }
        )
    }
    if (showEventHideDialog) {
        CustomAlertDialog(
            dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
            dialogTitle = "${stringResource(R.string.hide_event)}?",
            dialogText = stringResource(R.string.event_visibility_alert),
            onDismissRequest = {
                showEventHideDialog = false
            },
            onConfirmation = {
                scheduleActions.eventActions.onHideEvent(scheduleEntity, event.id)
                navigationActions.onBack()
            }
        )
    }
    if (showEventActionsDialog) {
        ModalDialogEvent(
            event = event,
            onEditEvent = if (event.isCustomEvent) {
                {
                    navigationActions.onBack()
                    navigationActions.navigateToEditEvent(scheduleEntity, event)
                }
            } else null,
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
                    scheduleActions.eventActions.onShowEvent(scheduleEntity, event.id)
                    event.isHidden = false
                }
            } else null
        ) {
            showEventActionsDialog = false
        }
    }
}