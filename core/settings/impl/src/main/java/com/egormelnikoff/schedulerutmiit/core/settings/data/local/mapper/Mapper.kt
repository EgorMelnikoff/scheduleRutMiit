package com.egormelnikoff.schedulerutmiit.core.settings.data.local.mapper

internal fun String.parseVersionCode(): Int {
    val clean = this.removePrefix("v")
    val parts = clean.split(".")

    val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

    return major * 10000 + minor * 100 + patch
}
