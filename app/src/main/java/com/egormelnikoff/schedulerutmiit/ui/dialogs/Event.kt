package com.egormelnikoff.schedulerutmiit.ui.dialogs

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.app.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints.personImageUrl
import com.egormelnikoff.schedulerutmiit.domain.schedule.EventAction
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomFilterChip
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingAsyncImage
import com.egormelnikoff.schedulerutmiit.ui.elements.ModalDialogEvent
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel
import kotlinx.coroutines.delay
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    event: Event,
    eventExtraData: EventExtraData?,
    namedScheduleEntity: NamedScheduleEntity,
    scheduleEntity: ScheduleEntity,
    isSavedSchedule: Boolean,
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current

    var showEventActionsDialog by remember { mutableStateOf(false) }
    var showEventDeleteDialog by remember { mutableStateOf(false) }
    var showEventHideDialog by remember { mutableStateOf(false) }

    var tag by remember { mutableIntStateOf(eventExtraData?.tag ?: 0) }
    var comment by remember { mutableStateOf(eventExtraData?.comment ?: "") }

    if (isSavedSchedule) {
        LaunchedEffect(comment, tag) {
            delay(500)
            scheduleViewModel.updateEventExtra(scheduleEntity, event, comment, tag)
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                navAction = {
                    appBackStack.onBack()
                },
                actions = {
                    IconButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    event.customToString(context)
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
                    bottom = innerPadding.calculateBottomPadding()
                )
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EventHeader(
                scheduleEntity, event, 16.dp
            )
            Spacer(modifier = Modifier.height(0.dp))
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp
                ),
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
                                                    it.combinedClickable(
                                                        onClick = {
                                                            appBackStack.navigateToStartRage()
                                                            appBackStack.onBack()
                                                            scheduleViewModel.fetchNamedSchedule(
                                                                group.name,
                                                                group.id,
                                                                NamedScheduleType.GROUP
                                                            )
                                                            searchViewModel.saveQueryToHistory(
                                                                SearchQuery(
                                                                    name = group.name,
                                                                    apiId = group.id,
                                                                    namedScheduleType = NamedScheduleType.GROUP
                                                                )
                                                            )
                                                        },
                                                        onLongClick = {
                                                            clipboard.nativeClipboard.setPrimaryClip(
                                                                ClipData.newPlainText(null, group.name)
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
                if (!event.rooms.isNullOrEmpty()) {
                    ColumnGroup(
                        title = stringResource(R.string.room),
                        titleColor = MaterialTheme.colorScheme.primary,
                        items = event.rooms.map { room ->
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
                if (!event.lecturers.isNullOrEmpty()) {
                    ColumnGroup(
                        title = stringResource(R.string.lecturers),
                        titleColor = MaterialTheme.colorScheme.primary,
                        items = event.lecturers.map { lecturer ->
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
                                    onClick = if (!event.isCustomEvent) {
                                        {
                                            appBackStack.navigateToStartRage()
                                            appBackStack.onBack()
                                            scheduleViewModel.fetchNamedSchedule(
                                                lecturer.fullFio,
                                                lecturer.id,
                                                NamedScheduleType.PERSON
                                            )
                                            searchViewModel.saveQueryToHistory(
                                                SearchQuery(
                                                    name = lecturer.fullFio,
                                                    apiId = lecturer.id,
                                                    namedScheduleType = NamedScheduleType.PERSON
                                                )
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
                                        visible = comment.isNotEmpty(),
                                        enter = scaleIn(animationSpec = tween(300)),
                                        exit = fadeOut(animationSpec = tween(500))
                                    ) {
                                        IconButton(
                                            onClick = {
                                                comment = ""
                                                scheduleViewModel.updateEventExtra(
                                                    scheduleEntity,
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
    }
    if (showEventActionsDialog) {
        ModalDialogEvent(
            scheduleEntity = scheduleEntity,
            event = event,
            onEditEvent = if (event.isCustomEvent) {
                {
                    appBackStack.onBack()
                    appBackStack.openDialog(
                        Route.Dialog.AddEventDialog(
                            namedScheduleEntity,
                            scheduleEntity,
                            event
                        )
                    )
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
                }
            } else null
        ) {
            showEventActionsDialog = false
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
                scheduleViewModel.eventAction(scheduleEntity, event, EventAction.Delete)
                appBackStack.onBack()
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
                scheduleViewModel.eventAction(scheduleEntity, event, EventAction.UpdateHidden(true))
                appBackStack.onBack()
            }
        )
    }
}

@Composable
fun EventHeader(
    scheduleEntity: ScheduleEntity,
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
                title = if (scheduleEntity.timetableType == TimetableType.PERIODIC) {
                    val dayName = event.startDatetime.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
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
            event.typeName?.let {
                CustomFilterChip(
                    title = event.typeName,
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