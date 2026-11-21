package com.egormelnikoff.schedulerutmiit.core.ui.components.text_field

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.VisualTransformation
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun AppTextField(
    modifier: Modifier,
    value: String,
    maxSymbols: Int = 200,
    keyboardOptions: KeyboardOptions? = null,
    colors: TextFieldColors? = null,
    maxLines: Int? = null,
    action: (() -> Unit)? = null,
    placeholderText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation? = null,
    onValueChanged: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value,
        maxLines = maxLines ?: Int.MAX_VALUE,
        modifier = modifier,
        colors = colors ?: TextFieldDefaults.colors(
            focusedContainerColor = AppTheme.colors.secondaryContainer,
            unfocusedContainerColor = AppTheme.colors.secondaryContainer,

            focusedTextColor = AppTheme.colors.onBackground,
            unfocusedTextColor = AppTheme.colors.onBackground,

            focusedIndicatorColor = AppTheme.colors.secondaryContainer,
            unfocusedIndicatorColor = AppTheme.colors.secondaryContainer,

            focusedLeadingIconColor = AppTheme.colors.onBackground,
            unfocusedLeadingIconColor = AppTheme.colors.primary,

            disabledContainerColor = AppTheme.colors.background,
            disabledIndicatorColor = AppTheme.colors.outline,
            disabledTextColor = AppTheme.colors.outline
        ),
        textStyle = AppTheme.typography.bodyLarge,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        placeholder = placeholderText?.let {
            {
                Text(
                    text = it,
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.onSecondaryContainer
                )
            }
        },
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        onValueChange = { newQuery ->
            if (newQuery.length <= maxSymbols) {
                onValueChanged(newQuery)
            }
        },
        shape = AppTheme.shapes.medium,
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            },
            onSearch = {
                action?.invoke()
                keyboardController?.hide()
            }
        )
    )
}