package com.egormelnikoff.schedulerutmiit.core.ui.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@Composable
fun AppDialog(
    dialogText: String,
    painter: Painter,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .background(
                    AppTheme.colors.background,
                    AppTheme.shapes.large
                )
                .padding(AppTheme.paddings.padding16),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding16)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(24.dp),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            Text(
                text = dialogText,
                color = AppTheme.colors.onBackground,
                style = AppTheme.typography.bodyLarge,
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = AppTheme.colors.primary,
                        contentColor = AppTheme.colors.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AppDialogPreview() {
    PreviewLightTheme {
        AppDialog(
            dialogText = "Очень большой текст для диалога",
            painter = painterResource(R.drawable.info),
            onDismissRequest = {}
        )
    }
}