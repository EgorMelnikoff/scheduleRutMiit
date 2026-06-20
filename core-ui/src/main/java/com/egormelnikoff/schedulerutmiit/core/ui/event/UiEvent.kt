package com.egormelnikoff.schedulerutmiit.core.ui.event

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.ui.elements.AppSnackbarVisuals

sealed interface UiEvent {
    data class ErrorMessage(
        val typedError: TypedError,
        val useSnackBar: Boolean = true
    ) : UiEvent

    data class InfoMessage(
        val message: UiText,
        val useSnackBar: Boolean = true
    ) : UiEvent
}

suspend fun UiEvent.handleUiEvent(
    context: Context,
    snackBarHostState: SnackbarHostState
) {
    val message = this.toMessage(context)
    when (this) {
        is UiEvent.ErrorMessage -> {
            if (this.useSnackBar) {
                snackBarHostState.showSnackbar(
                    AppSnackbarVisuals(
                        message = message,
                        isError = true,
                        duration = SnackbarDuration.Long
                    )
                )
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

        is UiEvent.InfoMessage -> {
            if (this.useSnackBar) {
                snackBarHostState.showSnackbar(
                    AppSnackbarVisuals(
                        message = message,
                        isError = false,
                        duration = SnackbarDuration.Short
                    )
                )
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun UiEvent.toMessage(context: Context): String {
    return when (this) {
        is UiEvent.ErrorMessage -> typedError.getMessage(context)
        is UiEvent.InfoMessage -> message.asString(context)
    }
}