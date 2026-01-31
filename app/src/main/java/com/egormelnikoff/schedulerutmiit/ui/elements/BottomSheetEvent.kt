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
import androidx.compose.ui.res.stringResource
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
        modifier = Modifier.padding(horizontal = 12.dp),
        onDismiss = {
            onDismiss(null)
        }
    ) {
        Text(
            text = event.name!!,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(0.dp))
        ColumnGroup(
            items = buildList {
                onHideEvent?.let {
                    add {
                        ActionDialogButton(
                            icon = ImageVector.vectorResource(R.drawable.visibility_off),
                            title = stringResource(R.string.hide_event),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            onHideEvent()
                            onDismiss(null)
                        }
                    }
                }
                onShowEvent?.let {
                    add {
                        ActionDialogButton(
                            icon = ImageVector.vectorResource(R.drawable.visibility),
                            title = stringResource(R.string.show_event),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            onShowEvent()
                            onDismiss(null)
                        }
                    }
                }
                onEditEvent?.let {
                    add {
                        ActionDialogButton(
                            icon = ImageVector.vectorResource(R.drawable.edit),
                            title = stringResource(R.string.edit),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            onEditEvent()
                            onDismiss(null)
                        }
                    }
                }
                onDeleteEvent?.let {
                    add {
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
        )
    }
}
