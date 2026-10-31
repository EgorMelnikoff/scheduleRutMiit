package com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.egormelnikoff.schedulerutmiit.core.common.utils.error.AppError
import com.egormelnikoff.schedulerutmiit.core.common.utils.error.getMessage

import com.egormelnikoff.schedulerutmiit.core.ui.components.snackbar_host.AppSnackbarVisuals
import kotlinx.coroutines.flow.MutableSharedFlow

sealed interface UiEvent {
    data class ErrorMessage(
        val appError: AppError,
        val useSnackBar: Boolean = true
    ) : UiEvent

    data class InfoMessage(
        val message: UiText,
        val useSnackBar: Boolean = true
    ) : UiEvent
}

suspend fun UiEvent.handle(
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
        is UiEvent.ErrorMessage -> appError.getMessage(context)
        is UiEvent.InfoMessage -> message.asString(context)
    }
}


suspend fun MutableSharedFlow<UiEvent>.sendErrorEvent(appError: AppError) {
    this.emit(
        UiEvent.ErrorMessage(appError)
    )
}
suspend fun MutableSharedFlow<UiEvent>.sendInfoEvent(infoMessage: UiEvent.InfoMessage) {
    this.emit(infoMessage)
}