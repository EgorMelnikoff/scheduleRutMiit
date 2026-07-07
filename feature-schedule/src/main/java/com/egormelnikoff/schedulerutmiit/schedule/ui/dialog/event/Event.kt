package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event

import android.content.ClipData
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints.personImageUrl
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomFilterChip
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LeadingAsyncImage
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.customToString
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.state.EventState
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.element.ModalDialogEvent
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    eventDialog: Route.Dialog.EventDialog,
    fetchNamedSchedule: (String, Int, NamedScheduleType) -> Unit,
    navigateToStartPage: () -> Unit,
    navigateToEditEventDialog: (Route.Dialog.EditEventDialog) -> Unit,
    onBack: () -> Unit
) {
    val eventViewModel = hiltViewModel<EventViewModel, EventViewModel.Factory> { factory ->
        factory.create(eventDialog.eventId, eventDialog.date)
    }
    val eventState = eventViewModel.eventState.collectAsStateWithLifecycle().value

    val context = LocalContext.current
    val clipboard = LocalClipboard.current

    var eventActionsDialog by remember { mutableStateOf(false) }
    var eventDeleteDialog by remember { mutableStateOf(false) }
    var eventHideDialog by remember { mutableStateOf(false) }

    when (eventState) {
        is EventState.Loading -> LoadingScreen()
        is EventState.Loaded -> {
            Scaffold(
                topBar = {
                    CustomTopAppBar(
                        navAction = onBack,
                        actions = {
                            IconButton(
                                onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            eventState.event.customToString(context)
                                        )
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

                            IconButton(
                                onClick = {
                                    eventActionsDialog = true
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
                    )
                }
            ) { innerPadding ->

                val groups = eventState.event.groups
                val rooms = eventState.event.rooms
                val lecturers = eventState.event.lecturers

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        EventHeader(
                            eventState.schedule.timetableType, eventState.event, 16.dp
                        )
                    }
                    if (!groups.isNullOrEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
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
                                            groups.forEach { group ->
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .clip(MaterialTheme.shapes.extraSmall)
                                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                                        .defaultMinSize(minWidth = 80.dp)
                                                        .let {
                                                            if (!eventState.event.isCustomEvent) {
                                                                it.combinedClickable(
                                                                    onClick = {
                                                                        fetchNamedSchedule(
                                                                            group.name,
                                                                            group.id,
                                                                            NamedScheduleType.GROUP
                                                                        )
                                                                        navigateToStartPage()
                                                                        onBack()
                                                                    },
                                                                    onLongClick = {
                                                                        clipboard.nativeClipboard.setPrimaryClip(
                                                                            ClipData.newPlainText(
                                                                                null,
                                                                                group.name
                                                                            )
                                                                        )
                                                                    }
                                                                )
                                                            } else it
                                                        }
                                                        .padding(8.dp)
                                                ) {
                                                    Text(
                                                        text = group.name,
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
                        }
                    }

                    if (!rooms.isNullOrEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                ColumnGroup(
                                    title = stringResource(R.string.room),
                                    titleColor = MaterialTheme.colorScheme.primary,
                                    items = rooms.map { room ->
                                        {
                                            ClickableItem(
                                                title = room.hint,
                                                titleMaxLines = 2,
                                                defaultMinHeight = 32.dp
                                            )
                                        }
                                    }

                                )
                            }
                        }
                    }

                    if (!lecturers.isNullOrEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                ColumnGroup(
                                    title = stringResource(R.string.lecturers),
                                    titleColor = MaterialTheme.colorScheme.primary,
                                    items = lecturers.map { lecturer ->
                                        {
                                            ClickableItem(
                                                title = lecturer.fullFio,
                                                titleMaxLines = 2,
                                                defaultMinHeight = 32.dp,
                                                onLongClick = {
                                                    clipboard.nativeClipboard.setPrimaryClip(
                                                        ClipData.newPlainText(
                                                            null,
                                                            lecturer.fullFio
                                                        )
                                                    )
                                                },
                                                onClick = if (!eventState.event.isCustomEvent) {
                                                    {
                                                        fetchNamedSchedule(
                                                            lecturer.fullFio,
                                                            lecturer.id,
                                                            NamedScheduleType.PERSON
                                                        )
                                                        navigateToStartPage()
                                                        onBack()
                                                    }
                                                } else null,
                                                leadingIcon = {
                                                    LeadingAsyncImage(
                                                        title = lecturer.fullFio,
                                                        imageUrl = personImageUrl(personId = lecturer.id)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }


                    item {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            ColumnGroup(
                                title = stringResource(R.string.comment),
                                titleColor = MaterialTheme.colorScheme.primary,
                                withBackground = false,
                                items = listOf {
                                    CustomTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        value = eventState.comment,
                                        keyboardOptions = KeyboardOptions(
                                            autoCorrectEnabled = false,
                                            imeAction = ImeAction.Default
                                        ),
                                        maxSymbols = 100,
                                        placeholderText = stringResource(R.string.enter_comment),
                                        trailingIcon = {
                                            AnimatedVisibility(
                                                visible = eventState.comment.isNotEmpty(),
                                                enter = scaleIn(animationSpec = tween(300)),
                                                exit = fadeOut(animationSpec = tween(500))
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        eventViewModel.updateComment("")
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
                                        eventViewModel.updateComment(newValue)
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            ColumnGroup(
                                title = stringResource(R.string.tag),
                                titleColor = MaterialTheme.colorScheme.primary,
                                withBackground = false,
                                items = listOf {
                                    ColorSelector(
                                        currentSelected = eventState.tag,
                                        onColorSelect = { newTag ->
                                            eventViewModel.updateTag(newTag)
                                        }
                                    )
                                }
                            )
                        }
                    }

                }
            }
            if (eventActionsDialog) {
                ModalDialogEvent(
                    timetableType = eventState.schedule.timetableType,
                    event = eventState.event,
                    onEditEvent = if (eventState.event.isCustomEvent) {
                        {
                            onBack()
                            navigateToEditEventDialog(
                                Route.Dialog.EditEventDialog(
                                    eventState.event.id,
                                    eventState.schedule.id
                                )
                            )
                        }
                    } else null,
                    onDeleteEvent = if (eventState.event.isCustomEvent) {
                        {
                            eventDeleteDialog = true
                        }
                    } else null,
                    onHideEvent = if (!eventState.event.isHidden) {
                        {
                            eventHideDialog = true
                        }
                    } else null
                ) {
                    eventActionsDialog = false
                }
            }
            if (eventDeleteDialog) {
                CustomAlertDialog(
                    dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                    dialogTitle = "${stringResource(R.string.delete_event)}?",
                    dialogText = stringResource(R.string.event_deleting_alert),
                    onDismissRequest = {
                        eventDeleteDialog = false
                    },
                    onConfirmation = {
                        eventViewModel.eventAction(
                            EventAction.Delete(eventDialog.eventId)
                        )
                    }
                )
            }
            if (eventHideDialog) {
                CustomAlertDialog(
                    dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
                    dialogTitle = "${stringResource(R.string.hide_event)}?",
                    dialogText = stringResource(R.string.event_visibility_alert),
                    onDismissRequest = {
                        eventHideDialog = false
                    },
                    onConfirmation = {
                        eventViewModel.eventAction(
                            EventAction.UpdateHidden(
                                eventDialog.eventId, false
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun EventHeader(
    timetableType: TimetableType,
    event: Event,
    horizontalPadding: Dp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = horizontalPadding
            ),
            text = event.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(
                    horizontal = horizontalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CustomFilterChip(
                imageVector = ImageVector.vectorResource(R.drawable.calendar),
                colors = FilterChipDefaults.filterChipColors(
                    disabledContainerColor = Color.Transparent,
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                ),
                border = BorderStroke(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                title = if (timetableType.isPeriodic()) {
                    val dayName = event.startDatetime.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        LocalLocale.current.platformLocale
                    ).toString().replaceFirstChar { it.uppercase() }
                    if (event.interval == 1) {
                        dayName
                    } else {
                        "$dayName, ${
                            stringResource(
                                R.string.week,
                                event.periodNumber ?: 0
                            ).replaceFirstChar { it.lowercase() }
                        }"
                    }
                } else "${
                    event.startDatetime.toLocalDate()
                        .format(dayMonthYearFormatter)
                }"
            )
            CustomFilterChip(
                imageVector = ImageVector.vectorResource(R.drawable.time),
                colors = FilterChipDefaults.filterChipColors(
                    disabledContainerColor = Color.Transparent,
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                ),
                border = BorderStroke(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                title = "${event.startDatetime.toLocalTimeWithTimeZone()} - ${event.endDatetime.toLocalTimeWithTimeZone()}"
            )
            val typeName = event.typeName
            typeName?.let {
                CustomFilterChip(
                    title = typeName,
                    border = BorderStroke(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        disabledContainerColor = Color.Transparent,
                        disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}