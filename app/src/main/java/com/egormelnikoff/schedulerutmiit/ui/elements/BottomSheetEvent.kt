package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogEvent(
    event: Event,
    onHideEvent: (() -> Unit)? = null,
    onDeleteEvent: (() -> Unit)? = null,
    onEditEvent: (() -> Unit)? = null,
    onShowEvent: (() -> Unit)? = null,
    onDismiss: (Event?) -> Unit
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(0.dp))
        onHideEvent?.let {
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
        onShowEvent?.let {
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
        onEditEvent?.let {
            ActionDialogButton(
                onClick = {
                    onEditEvent()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.edit),
                title = LocalContext.current.getString(R.string.edit),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        onDeleteEvent?.let {
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
