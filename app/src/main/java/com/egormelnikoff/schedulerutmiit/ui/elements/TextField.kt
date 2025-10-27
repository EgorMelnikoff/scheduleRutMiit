package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CustomTextField(
    modifier: Modifier,
    value: String,
    onValueChanged: ((String) -> Unit)? = null,
    keyboardOptions: KeyboardOptions? = null,
    colors: TextFieldColors? = null,
    maxLines: Int? = null,
    action: (() -> Unit)? = null,
    placeholderText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value,
        maxLines = maxLines ?: Int.MAX_VALUE,
        modifier = modifier,
        colors = colors ?: TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,

            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,

            focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,

            focusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,

            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline,
            disabledTextColor = MaterialTheme.colorScheme.outline
        ),
        textStyle = MaterialTheme.typography.titleSmall,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        placeholder = placeholderText?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        onValueChange = { newQuery ->
            onValueChanged?.let {
                it(newQuery)
            }
        },
        shape = MaterialTheme.shapes.medium,
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