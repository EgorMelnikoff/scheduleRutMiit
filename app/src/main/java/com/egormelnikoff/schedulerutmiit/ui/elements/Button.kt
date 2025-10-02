package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    imageVector: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = enabled,
        border = if (!enabled) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface
            )
        } else null,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ){
            if (imageVector != null){
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = imageVector,
                    contentDescription = null
                )
            }

            Text(
                text = buttonTitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}