package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet (
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.outline
            )
        },
        sheetState = sheetState ?: rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = horizontalAlignment
        ) {
            content.invoke()
        }
    }
}
