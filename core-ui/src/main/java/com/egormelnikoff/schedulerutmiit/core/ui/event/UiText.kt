package com.egormelnikoff.schedulerutmiit.core.ui.event

import android.content.Context
import androidx.annotation.StringRes

sealed interface UiText {
    data class DynamicString(
        val value: String
    ) : UiText

    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResource -> context.getString(resId, *args.toTypedArray())
    }
}