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
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = PhantomSans),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = PhantomSans),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = PhantomSans),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = PhantomSans),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = PhantomSans),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = PhantomSans),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = PhantomSans),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = PhantomSans), // Seems to be default for Text("")
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = PhantomSans),
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = PhantomSans),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = PhantomSans),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = PhantomSans),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = PhantomSans),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = PhantomSans),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = PhantomSans)
)