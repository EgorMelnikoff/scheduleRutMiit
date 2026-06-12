package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog

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
import androidx.compose.runtime.mutableIntStateOf
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
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.customToString
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.event.ModalDialogEvent
import java.time.LocalDateTime
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    eventDialog: Route.Dialog.EventDialog,
    currentDateTime: LocalDateTime,
    fetchNamedSchedule: (String, Int, NamedScheduleType) -> Unit,
    updateEventComment: (Long, Event, LocalDateTime, String) -> Unit,
    updateEventTag: (Long, Event, LocalDateTime, Int) -> Unit,
    deleteEvent: (Long, Long) -> Unit,
    hideEvent: (Long, Long) -> Unit,

    navigateToEditEventDialog: (Route.Dialog.AddEditEventDialog) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current

    var eventActionsDialog by remember { mutableStateOf(false) }
    var eventDeleteDialog by remember { mutableStateOf(false) }
    var eventHideDialog by remember { mutableStateOf(false) }

    var tag by remember { mutableIntStateOf(eventDialog.eventExtraData?.tag ?: 0) }
    var comment by remember { mutableStateOf(eventDialog.eventExtraData?.comment ?: "") }

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
                                    eventDialog.event.customToString(context)
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
                    if (eventDialog.isSavedSchedule) {
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
                }
            )
        }
    ) { innerPadding ->
        val groups = eventDialog.event.groups
        val rooms = eventDialog.event.rooms
        val lecturers = eventDialog.event.lecturers

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
                    eventDialog.schedule.timetableType, eventDialog.event, 16.dp
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
                                                    if (!eventDialog.event.isCustomEvent) {
                                                        it.combinedClickable(
                                                            onClick = {
                                                                fetchNamedSchedule(
                                                                    group.name,
                                                                    group.id,
                                                                    NamedScheduleType.GROUP
                                                                )
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
//                                onClick = if (!event.isCustomEvent) {
//                                    {
//                                        navigationActions.navigateToSchedule()
//                                        navigationActions.onBack()
//                                        scheduleActions.onGetNamedSchedule(
//                                            room.name,
//                                            room.id,
//                                            NamedScheduleType.ROOM
//                                        )
//                                        searchViewModel.saveQueryToHistory(
//                                            SearchQuery(
//                                                name = room.name,
//                                                apiId = room.id,
//                                                namedScheduleType = NamedScheduleType.ROOM
//                                            )
//                                        )
//                                    }
//                                } else null
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
                                                ClipData.newPlainText(null, lecturer.fullFio)
                                            )
                                        },
                                        onClick = if (!eventDialog.event.isCustomEvent) {
                                            {
                                                fetchNamedSchedule(
                                                    lecturer.fullFio,
                                                    lecturer.id,
                                                    NamedScheduleType.PERSON
                                                )
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

            if (eventDialog.isSavedSchedule) {
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
                                    value = comment,
                                    keyboardOptions = KeyboardOptions(
                                        autoCorrectEnabled = false,
                                        imeAction = ImeAction.Default
                                    ),
                                    maxSymbols = 100,
                                    placeholderText = stringResource(R.string.enter_comment),
                                    trailingIcon = {
                                        AnimatedVisibility(
                                            visible = comment.isNotEmpty(),
                                            enter = scaleIn(animationSpec = tween(300)),
                                            exit = fadeOut(animationSpec = tween(500))
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    comment = ""
                                                    updateEventComment(
                                                        eventDialog.schedule.id,
                                                        eventDialog.event,
                                                        currentDateTime,
                                                        comment
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
                                    updateEventComment(
                                        eventDialog.schedule.id,
                                        eventDialog.event,
                                        currentDateTime,
                                        newValue
                                    )
                                }
                            }
                        )
                    }
                }
            }

            if (eventDialog.isSavedSchedule) {
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
                                    currentSelected = tag,
                                    onColorSelect = { newTag ->
                                        tag = newTag
                                        updateEventTag(
                                            eventDialog.schedule.id,
                                            eventDialog.event,
                                            currentDateTime,
                                            newTag
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
    if (eventActionsDialog) {
        ModalDialogEvent(
            timetableType = eventDialog.schedule.timetableType,
            event = eventDialog.event,
            onEditEvent = if (eventDialog.event.isCustomEvent) {
                {
                    onBack()
                    navigateToEditEventDialog(
                        Route.Dialog.AddEditEventDialog(
                            eventDialog.namedScheduleId,
                            eventDialog.schedule.id,
                            eventDialog.schedule.recurrence,
                            eventDialog.schedule.startDate,
                            eventDialog.schedule.endDate,
                            eventDialog.event
                        )
                    )
                }
            } else null,
            onDeleteEvent = if (eventDialog.event.isCustomEvent) {
                {
                    eventDeleteDialog = true
                }
            } else null,
            onHideEvent = if (!eventDialog.event.isHidden) {
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
                deleteEvent(
                    eventDialog.namedScheduleId, eventDialog.event.id
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
                hideEvent(eventDialog.namedScheduleId, eventDialog.event.id)
            }
        )
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
                title = if (timetableType == TimetableType.PERIODIC) {
                    val dayName = event.startDatetime.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        LocalLocale.current.platformLocale
                    ).toString().replaceFirstChar { it.uppercase() }
                    if (event.recurrenceRule?.interval == 1) {
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