package com.egormelnikoff.schedulerutmiit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        textMotion = TextMotion.Static,
        letterSpacing = 0.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        textMotion = TextMotion.Static,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        textMotion = TextMotion.Static,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        textMotion = TextMotion.Static,
        fontSize = 8.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        letterSpacing = 0.sp
    ),

    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textMotion = TextMotion.Static,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        textMotion = TextMotion.Static,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        textMotion = TextMotion.Static,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        letterSpacing = 0.sp
    )
)