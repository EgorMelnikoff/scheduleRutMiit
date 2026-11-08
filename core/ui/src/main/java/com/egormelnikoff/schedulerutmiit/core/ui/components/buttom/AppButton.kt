package com.egormelnikoff.schedulerutmiit.core.ui.components.buttom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    imageVector: ImageVector? = null,
    enabled: Boolean = true,
    titleStyle: TextStyle = AppTheme.typography.titleSmall,
    buttonType: ButtonType = ButtonType.FILLED,
    shape: Shape? = null,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        border = if (!enabled) {
            BorderStroke(
                width = 1.dp,
                color = AppTheme.colors.secondaryContainer
            )
        } else null,
        shape = shape ?: AppTheme.shapes.large,
        colors = when (buttonType) {
            ButtonType.FILLED -> ButtonColors(
                containerColor = AppTheme.colors.primary,
                contentColor = AppTheme.colors.onPrimary,
                disabledContainerColor = AppTheme.colors.background,
                disabledContentColor = AppTheme.colors.outline
            )

            ButtonType.OUTLINED -> ButtonColors(
                containerColor = AppTheme.colors.background,
                contentColor = AppTheme.colors.onBackground,
                disabledContainerColor = AppTheme.colors.background,
                disabledContentColor = AppTheme.colors.outline
            )

            ButtonType.ELEVATED -> ButtonColors(
                containerColor = AppTheme.colors.secondaryContainer,
                contentColor = AppTheme.colors.onBackground,
                disabledContainerColor = AppTheme.colors.background,
                disabledContentColor = AppTheme.colors.outline
            )
        }
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4, Alignment.CenterHorizontally),
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
                style = titleStyle
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppButtonPreview() {
    PreviewLightTheme {
        AppButton(
            buttonTitle = "Кнопка"
        ) {}
    }
}

enum class ButtonType {
    OUTLINED, FILLED, ELEVATED
}