package com.egormelnikoff.schedulerutmiit.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNamedScheduleActions(
    onDismiss: (NamedScheduleEntity?) -> Unit,
    onSetSchedule: (() -> Unit)? = null,
    onSelectDefault: () -> Unit,
    onDelete: () -> Unit,
    onLoadInitialData: (() -> Unit)? = null,
    namedScheduleEntity: NamedScheduleEntity,
) {
    CustomModalBottomSheet(
        modifier = Modifier.padding(horizontal = 8.dp),
        onDismiss = {
            onDismiss(null)
        }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = namedScheduleEntity.fullName,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (!namedScheduleEntity.isDefault) {
            ActionDialogButton(
                onClick = {
                    onSelectDefault()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.check),
                title = LocalContext.current.getString(R.string.make_default),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        if (onSetSchedule != null) {
            ActionDialogButton(
                onClick = {
                    onSetSchedule()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.open),
                title = LocalContext.current.getString(R.string.open),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }

        ActionDialogButton(
            onClick = {
                onDelete()
                onDismiss(null)
            },
            icon = ImageVector.vectorResource(R.drawable.delete),
            title = LocalContext.current.getString(R.string.delete),
            contentColor = MaterialTheme.colorScheme.error
        )
        if (onLoadInitialData != null) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline
            )
            ActionDialogButton(
                onClick = {
                    onLoadInitialData()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.back),
                title = LocalContext.current.getString(R.string.return_default),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogEventActions(
    onDismiss: (Event?) -> Unit,
    event: Event,
    onHideEvent: (() -> Unit)? = null,
    onDeleteEvent: (() -> Unit)? = null,
    onShowEvent: (() -> Unit)? = null
) {
    CustomModalBottomSheet(
        modifier = Modifier.padding(horizontal = 8.dp),
        onDismiss = {
            onDismiss(null)
        }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = event.name!!,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (onHideEvent != null) {
            ActionDialogButton(
                onClick = {
                    onHideEvent()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.visibility_off),
                title = LocalContext.current.getString(R.string.hide_event),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        if (onShowEvent != null) {
            ActionDialogButton(
                onClick = {
                    onShowEvent()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.visibility),
                title = LocalContext.current.getString(R.string.show_event),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        if (onDeleteEvent != null) {
            ActionDialogButton(
                onClick = {
                    onDeleteEvent()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.delete),
                title = LocalContext.current.getString(R.string.delete_event),
                contentColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun ActionDialogButton(
    onClick: () -> Unit,
    icon: ImageVector,
    title: String,
    contentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
        Text(
            text = title,
            fontSize = 14.sp,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
