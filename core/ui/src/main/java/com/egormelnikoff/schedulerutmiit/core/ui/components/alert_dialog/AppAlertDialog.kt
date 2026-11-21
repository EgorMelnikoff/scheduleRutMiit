package com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dismissOnConfirm: Boolean = true,
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
        icon = dialogIcon?.let {
            {
                Icon(
                    imageVector = dialogIcon,
                    contentDescription = dialogTitle
                )
            }

        },
        text = {
            Text(
                text = dialogText,
                style = AppTheme.typography.bodyLarge,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                    if (dismissOnConfirm) onDismissRequest()
                }
            ) {
                Text(
                    text = confirmText ?: stringResource(R.string.yes),
                    style = AppTheme.typography.bodyLarge,
                    color = AppTheme.colors.primary
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = dismissText ?: stringResource(R.string.no),
                    style = AppTheme.typography.bodyLarge,
                    color = AppTheme.colors.primary
                )
            }
        },
        containerColor = AppTheme.colors.background,
        titleContentColor = AppTheme.colors.onBackground,
        textContentColor = AppTheme.colors.onSecondaryContainer,
        iconContentColor = AppTheme.colors.onSecondaryContainer
    )
}