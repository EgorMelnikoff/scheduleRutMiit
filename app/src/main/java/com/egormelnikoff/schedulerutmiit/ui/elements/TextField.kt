package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,

            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,

            focusedIndicatorColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,

            focusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,

            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline,
            disabledTextColor = MaterialTheme.colorScheme.outline
        ),
        textStyle = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        ),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        placeholder = if (placeholderText != null){
            {
                Text(
                    text = placeholderText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else null,
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        onValueChange = { newQuery ->
            if (onValueChanged != null) {
                onValueChanged(newQuery)
            }
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            },
            onSearch = {
                if (action != null) {
                    action()
                }
                keyboardController?.hide()
            }
        )
    )
}