package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.dialogs.EventHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogEvent(
    scheduleEntity: ScheduleEntity,
    event: Event,
    onHideEvent: (() -> Unit)? = null,
    onDeleteEvent: (() -> Unit)? = null,
    onEditEvent: (() -> Unit)? = null,
    onDismiss: (Event?) -> Unit
) {
    CustomModalBottomSheet(
        onDismiss = {
            onDismiss(null)
        }
    ) {
        EventHeader(
            scheduleEntity, event, 16.dp
        )
        Spacer(modifier = Modifier.height(0.dp))
        onHideEvent?.let {
            ActionDialogButton(
                icon = ImageVector.vectorResource(R.drawable.visibility_off),
                title = stringResource(R.string.hide_event),
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                onHideEvent()
                onDismiss(null)
            }
        }
        onEditEvent?.let {
            ActionDialogButton(
                icon = ImageVector.vectorResource(R.drawable.edit),
                title = stringResource(R.string.edit),
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                onEditEvent()
                onDismiss(null)
            }
        }
        onDeleteEvent?.let {
            ActionDialogButton(
                icon = ImageVector.vectorResource(R.drawable.delete),
                title = stringResource(R.string.delete_event),
                contentColor = MaterialTheme.colorScheme.error
            ) {
                onDeleteEvent()
                onDismiss(null)
            }
        }
    }
}
