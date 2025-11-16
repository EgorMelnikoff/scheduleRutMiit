package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        dragHandle = {
            Surface(
                modifier = modifier
                    .padding(vertical = 24.dp)
                    .clickable(
                        interactionSource = null,
                        enabled = false,
                        onClick = {}
                    ),
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraSmall,
            ) {
                Box(Modifier.size(width = 36.dp, height = 4.dp))
            }
        },
        sheetState = sheetState ?: rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content.invoke()
        }
    }
}
