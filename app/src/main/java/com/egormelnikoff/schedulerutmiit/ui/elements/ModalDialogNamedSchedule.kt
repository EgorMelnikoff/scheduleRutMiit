package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogNamedSchedule(
    navigateToRenameDialog: () -> Unit,
    onDismiss: (NamedScheduleEntity?) -> Unit,
    onSetSchedule: (() -> Unit)? = null,
    onSelectDefault: () -> Unit,
    onDelete: () -> Unit,
    onLoadInitialData: (() -> Unit)? = null,
    onSaveCurrentNamedSchedule: (() -> Unit)? = null,
    namedScheduleEntity: NamedScheduleEntity,
    isSavedNamedSchedule: Boolean,
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(0.dp))
        if (!namedScheduleEntity.isDefault && isSavedNamedSchedule) {
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
        onSetSchedule?.let {
            ActionDialogButton(
                onClick = {
                    it()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.open),
                title = LocalContext.current.getString(R.string.open),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
        if (isSavedNamedSchedule) {
            ActionDialogButton(
                onClick = {
                    navigateToRenameDialog()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.edit),
                title = LocalContext.current.getString(R.string.rename),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
            ActionDialogButton(
                onClick = {
                    onDelete()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.delete),
                title = LocalContext.current.getString(R.string.delete),
                contentColor = MaterialTheme.colorScheme.error
            )
        } else if (onSaveCurrentNamedSchedule != null) {
            ActionDialogButton(
                onClick = {
                    onSaveCurrentNamedSchedule()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.save),
                title = LocalContext.current.getString(R.string.save),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }

        onLoadInitialData?.let {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline
            )
            ActionDialogButton(
                onClick = {
                    it()
                    onDismiss(null)
                },
                icon = ImageVector.vectorResource(R.drawable.back),
                title = LocalContext.current.getString(R.string.return_default),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}