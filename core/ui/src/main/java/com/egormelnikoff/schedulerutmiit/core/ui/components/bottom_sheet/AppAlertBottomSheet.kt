package com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.ButtonType
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAlertBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dismissOnConfirm: Boolean = true,
    dialogTitle: String,
    dialogText: String,
    confirmText: String = stringResource(R.string._continue),
    dismissText: String = stringResource(R.string.cancel)
) {
    AppBottomSheet(
        showHandle = false,
        onDismiss = onDismissRequest,
        contentPadding = PaddingValues(
            top = 40.dp,
            start = AppTheme.paddings.padding32,
            end = AppTheme.paddings.padding32,
            bottom = AppTheme.paddings.padding12
        ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = dialogTitle,
            color = AppTheme.colors.onBackground,
            style = AppTheme.typography.titleLarge,
        )
        SpacerHeight(AppTheme.paddings.padding12)
        Text(
            text = dialogText,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.onSecondaryContainer,
            style = AppTheme.typography.bodyLarge,
        )

        SpacerHeight(24.dp)

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            titleStyle = AppTheme.typography.titleMedium,
            shape = AppTheme.shapes.medium,
            buttonTitle = confirmText
        ) {
            onConfirmation()
            if (dismissOnConfirm) onDismissRequest()
        }
        SpacerHeight(AppTheme.paddings.padding2)
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            buttonTitle = dismissText,
            titleStyle = AppTheme.typography.titleMedium,
            shape = AppTheme.shapes.medium,
            buttonType = ButtonType.ELEVATED,
        ) {
            onDismissRequest()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AppAlertDialogPreview() {
    PreviewLightTheme {
        AppAlertBottomSheet(
            onDismissRequest = {},
            onConfirmation = {},
            dialogTitle = "Скрыть пару?",
            dialogText = "Будьте внимательны, занятие перестанет отображаться в расписании",
            confirmText = "Скрыть",
            dismissText = "Отмена"
        )
    }
}