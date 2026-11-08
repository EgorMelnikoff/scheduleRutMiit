package com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean? = null,
    sheetState: SheetState? = null,
    showHandle: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        bottom = AppTheme.paddings.padding12
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AppTheme.paddings.padding8),
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        dragHandle = {
            if (showHandle) {
                Surface(
                    modifier = Modifier
                        .padding(vertical = AppTheme.paddings.padding24)
                        .clickable(
                            interactionSource = null,
                            enabled = false,
                            onClick = {}
                        ),
                    color = AppTheme.colors.outline,
                    shape = AppTheme.shapes.extraSmall,
                ) {
                    Box(Modifier.size(width = 36.dp, height = 4.dp))
                }
            }
        },
        sheetState = sheetState ?: rememberModalBottomSheetState(),
        containerColor = AppTheme.colors.background,
        onDismissRequest = onDismiss
    ) {
        isDarkTheme?.let {
            val view = LocalView.current
            val dialogWindow = (view.parent as? DialogWindowProvider)?.window

            SideEffect {
                dialogWindow?.let {
                    val insetsController = WindowCompat.getInsetsController(dialogWindow, view)
                    insetsController.isAppearanceLightStatusBars = !isDarkTheme
                }
            }
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            content = content
        )
    }
}
