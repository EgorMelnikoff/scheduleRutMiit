package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.egormelnikoff.schedulerutmiit.R

@Composable
fun AdviceDialog(
    onDismissRequest: () -> Unit,
    dialogText: String,
    painter: Painter
){
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.shapes.large
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            Text(
                text = dialogText,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    }
}