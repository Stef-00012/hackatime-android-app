package com.stefdp.hackatime.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.stefdp.hackatime.R

val PhantomSans = FontFamily(
    Font(R.font.phantom_sans)
)
val defaultTypography = Typography()


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PhantomSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = PhantomSans),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = PhantomSans),
    titleLarge = TextStyle(
        fontFamily = PhantomSans,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = PhantomSans),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = PhantomSans),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = PhantomSans),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = PhantomSans), // Seems to be default for Text("")
    labelSmall = TextStyle(
        fontFamily = PhantomSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = PhantomSans),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = PhantomSans),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = PhantomSans),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = PhantomSans),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = PhantomSans),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = PhantomSans)
)