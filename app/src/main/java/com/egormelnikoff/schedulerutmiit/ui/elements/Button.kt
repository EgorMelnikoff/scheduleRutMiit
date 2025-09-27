package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    modifier: Modifier,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = if (!enabled) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface
            )
        } else null,
        colors = ButtonDefaults.outlinedButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.surface
        ),
        enabled = enabled,
        onClick = onClick
    ) {
        Text(
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}