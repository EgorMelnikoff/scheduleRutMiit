package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R

@Composable
fun CustomSnackbarHost(
    snackBarHostState: SnackbarHostState
) {
    SnackbarHost(
        modifier = Modifier.padding(horizontal = 24.dp),
        hostState = snackBarHostState
    ) { data ->
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = data.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconButton(
                onClick = {
                    data.dismiss()
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.clear),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}