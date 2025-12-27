package com.egormelnikoff.schedulerutmiit.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val baseItemSpacing = 2.dp
val superExtraSmallCornerRadius = 2.dp
val extraSmallCornerRadius = 4.dp
val smallCornerRadius = 8.dp
val mediumCornerRadius = 12.dp
val largeCornerRadius = 16.dp
val extraLargeCornerRadius = 20.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(extraSmallCornerRadius),
    small = RoundedCornerShape(smallCornerRadius),
    medium = RoundedCornerShape(mediumCornerRadius),
    large = RoundedCornerShape(largeCornerRadius),
    extraLarge = RoundedCornerShape(extraLargeCornerRadius)
)