package com.egormelnikoff.schedulerutmiit.app.widget.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.glance.LocalContext

@Composable
@ReadOnlyComposable
fun glanceStringResource(@StringRes id: Int): String {
    return LocalContext.current.getString(id)
}
