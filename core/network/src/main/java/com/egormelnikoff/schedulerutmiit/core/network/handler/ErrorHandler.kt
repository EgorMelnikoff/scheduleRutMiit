package com.egormelnikoff.schedulerutmiit.core.network.handler

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler

inline fun errorHandler(
    tag: String,
    crossinline onError: (Throwable) -> Unit = {}
): CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.e(tag, throwable.localizedMessage, throwable)
    onError(throwable)
}