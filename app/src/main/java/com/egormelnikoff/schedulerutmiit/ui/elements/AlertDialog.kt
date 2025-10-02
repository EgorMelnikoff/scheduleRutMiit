package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.egormelnikoff.schedulerutmiit.R

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    dialogIcon: ImageVector? = null,
    confirmText: String? = null,
    dismissText: String? = null
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        icon = if (dialogIcon != null){
            {
                Icon(
                    imageVector = dialogIcon,
                    contentDescription = dialogTitle
                )
            }

        } else null,
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                    onDismissRequest()
                }
            ) {
                Text(
                    text = confirmText ?: LocalContext.current.getString(R.string.yes),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = dismissText ?: LocalContext.current.getString(R.string.no),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        iconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        textContentColor = MaterialTheme.colorScheme.onSurface,
    )
}