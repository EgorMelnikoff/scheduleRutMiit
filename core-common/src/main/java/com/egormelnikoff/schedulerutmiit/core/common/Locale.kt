package com.egormelnikoff.schedulerutmiit.core.common

import android.os.Build
import java.util.Locale

object Locale {
    val ruLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        Locale.of("ru", "RU")
    } else {
        @Suppress("DEPRECATION")
        Locale("ru", "RU")
    }
}