package com.egormelnikoff.schedulerutmiit.ui.widget.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.glance.LocalContext

@Composable
@ReadOnlyComposable
fun glanceStringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    return LocalContext.current.getString(id, *formatArgs)
}
